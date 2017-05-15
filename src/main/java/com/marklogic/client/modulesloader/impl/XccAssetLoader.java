package com.marklogic.client.modulesloader.impl;

import com.marklogic.client.helper.LoggingObject;
import com.marklogic.client.modulesloader.ModulesManager;
import com.marklogic.client.modulesloader.tokenreplacer.ModuleTokenReplacer;
import com.marklogic.client.modulesloader.xcc.CommaDelimitedPermissionsParser;
import com.marklogic.client.modulesloader.xcc.DefaultDocumentFormatGetter;
import com.marklogic.client.modulesloader.xcc.DocumentFormatGetter;
import com.marklogic.client.modulesloader.xcc.PermissionsParser;
import com.marklogic.xcc.*;
import com.marklogic.xcc.exceptions.RequestException;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * <p>
 *     Handles loading assets - as defined by the REST API, which are typically under the /ext directory - via XCC.
 *     Currently not a threadsafe class - in order to make it threadsafe, would need to move the impl of FileVisitor to
 *     a separate class.
 * </p>
 * <p>
 *     Version 2.11.0 introduced the ability to bulk load modules by loading all modules in one XCC
 *     request. This is usually more efficient, so it's set to true by default.
 * </p>
 */
public class XccAssetLoader extends LoggingObject implements FileVisitor<Path> {

    // XCC connection info
    private String username;
    private String password;
    private String host;
    private Integer port = 8000;
    private String databaseName;
    private SecurityOptions securityOptions;

    // Controls what files/directories are processed
    private FileFilter fileFilter = new AssetFileFilter();

    // Default permissions and queryCollections for each module
    private String permissions = "rest-admin,read,rest-admin,update,rest-extension-user,execute";
    private String[] collections;

    private PermissionsParser permissionsParser = new CommaDelimitedPermissionsParser();
    private DocumentFormatGetter documentFormatGetter = new DefaultDocumentFormatGetter();

    // Whether to load modules in a single request or not
    private boolean bulkLoad = true;

    // If bulkLoad is set to true, keeps track of all the modules to be loaded
    private List<Content> bulkContents;

    // State that is maintained while visiting each asset path. Would need to move this to another class if this
    // class ever needs to be thread-safe.
    private Session activeSession;
    private Path currentAssetPath;
    private Path currentRootPath;
	private List<LoadedAsset> loadedAssets;

    // Manages when modules were last loaded
    private ModulesManager modulesManager;

    private ModuleTokenReplacer moduleTokenReplacer;

    /**
     * For walking one or many paths and loading modules in each of them.
     */
    public List<LoadedAsset> loadAssetsViaXcc(String... paths) {
        initializeActiveSession();
		loadedAssets = new ArrayList<>();
        try {
            for (String path : paths) {
                if (logger.isDebugEnabled()) {
                    logger.debug(format("Loading assets from path: %s", path));
                }
                this.currentAssetPath = Paths.get(path);
                this.currentRootPath = this.currentAssetPath;
                if (bulkLoad) {
                    bulkContents = new ArrayList<>();
                }
                try {
                    Files.walkFileTree(this.currentAssetPath, this);
                } catch (IOException ie) {
                    throw new RuntimeException(format("IO error while walking assets file tree: %s", ie.getMessage()), ie);
                }
				if (bulkLoad) {
					try {
						activeSession.insertContent(bulkContents.toArray(new Content[]{}));
					} catch (RequestException ex) {
						throw new RuntimeException("Unable to complete bulk load, cause: " + ex.getMessage(), ex);
					}
				}
            }
            return loadedAssets;
        } finally {
            closeActiveSession();
        }
    }

    /**
     * Initialize the XCC session.
     */
    protected void initializeActiveSession() {
        if (logger.isDebugEnabled()) {
            if (databaseName != null) {
                logger.debug(format("Initializing XCC session; host: %s; username: %s; database name: %s", host,
                        username, databaseName));
            } else {
                logger.debug(format("Initializing XCC session; host: %s; username: %s", host, username));
            }
        }

        ContentSource cs = ContentSourceFactory.newContentSource(host, port, username, password, databaseName,
                securityOptions);
        activeSession = cs.newSession();
    }

    /**
     * Close the XCC session.
     */
    protected void closeActiveSession() {
        if (activeSession != null) {
            logger.debug("Closing XCC session");
            activeSession.close();
            activeSession = null;
        }
    }

    /**
     * FileVisitor method that determines if we should visit the directory or not via the fileFilter.
     */
    @Override
    public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attributes) throws IOException {
        boolean accept = fileFilter.accept(path.toFile());
        if (accept) {
            if (logger.isTraceEnabled()) {
                logger.trace("Visiting directory: " + path);
            }
            return FileVisitResult.CONTINUE;
        } else {
            if (logger.isTraceEnabled()) {
                logger.trace("Skipping directory: " + path);
            }
            return FileVisitResult.SKIP_SUBTREE;
        }
    }

    /**
     * FileVisitor method that loads the file into the modules database if the fileFilter accepts it.
     */
    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) throws IOException {
        if (fileFilter.accept(path.toFile())) {
            Path relPath = currentAssetPath.relativize(path);
            String uri = "/" + relPath.toString().replace("\\", "/");
            if (this.currentRootPath != null) {
                String name = this.currentRootPath.toFile().getName();
                // A bit of a hack to support the special "root" directory.
                if (!"root".equals(name)) {
                    uri = "/" + name + uri;
                }
            }
			File f = path.toFile();
            Content c = loadFile(uri, f);
			if (c != null) {
				loadedAssets.add(new LoadedAsset(c.getUri(), f, moduleCanBeReadAsString(c.getCreateOptions().getFormat())));
			}
        }

        return FileVisitResult.CONTINUE;
    }

    /**
     * Does the actual work of loading a file into the modules database via XCC.
     *
     * @param uri
     * @param f
     */
    protected Content loadFile(String uri, File f) {
        if (modulesManager != null && !modulesManager.hasFileBeenModifiedSinceLastInstalled(f)) {
            return null;
        }

        ContentCreateOptions options = new ContentCreateOptions();
        options.setFormat(documentFormatGetter.getDocumentFormat(f));
        options.setPermissions(permissionsParser.parsePermissions(this.permissions));
        if (this.collections != null) {
            options.setCollections(collections);
        }

        if (logger.isInfoEnabled()) {
            logger.info(format("Inserting module at URI: %s", uri));
        }

        Content content = buildContent(uri, f, options);
        try {
            if (bulkLoad) {
                bulkContents.add(content);
            } else {
                activeSession.insertContent(content);
            }
            if (modulesManager != null) {
                modulesManager.saveLastInstalledTimestamp(f, new Date());
            }
			return content;
        } catch (RequestException re) {
            throw new RuntimeException("Unable to insert content at URI: " + uri + "; cause: " + re.getMessage(), re);
        }
    }

    /**
     * If we have a ModuleTokenReplacer, we try to use it. But if we can't load the file as a string, we just assume we
     * can't replace any tokens in it.
     *
     * @param uri
     * @param f
     * @param options
     * @return
     */
    protected Content buildContent(String uri, File f, ContentCreateOptions options) {
        Content content = null;
        if (moduleTokenReplacer != null && moduleCanBeReadAsString(options.getFormat())) {
            try {
                String text = new String(FileCopyUtils.copyToByteArray(f));
                text = moduleTokenReplacer.replaceTokensInModule(text);
                content = ContentFactory.newContent(uri, text, options);
            } catch (IOException ie) {
                content = ContentFactory.newContent(uri, f, options);
            }
        } else {
            content = ContentFactory.newContent(uri, f, options);
        }
        return content;
    }

    protected boolean moduleCanBeReadAsString(DocumentFormat format) {
        return format != null && (format.equals(DocumentFormat.JSON) || format.equals(DocumentFormat.TEXT)
                || format.equals(DocumentFormat.XML));
    }

    @Override
    public FileVisitResult postVisitDirectory(Path path, IOException exception) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path path, IOException exception) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public void setCollections(String[] collections) {
        this.collections = collections;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public void setPermissionsParser(PermissionsParser permissionsParser) {
        this.permissionsParser = permissionsParser;
    }

    public void setDocumentFormatGetter(DocumentFormatGetter documentFormatGetter) {
        this.documentFormatGetter = documentFormatGetter;
    }

    public void setModulesManager(ModulesManager modulesManager) {
        this.modulesManager = modulesManager;
    }

    public void setFileFilter(FileFilter fileFilter) {
        this.fileFilter = fileFilter;
    }

    public void setModuleTokenReplacer(ModuleTokenReplacer moduleTokenReplacer) {
        this.moduleTokenReplacer = moduleTokenReplacer;
    }

    public ModuleTokenReplacer getModuleTokenReplacer() {
        return moduleTokenReplacer;
    }

	public void setBulkLoad(boolean bulkLoad) {
		this.bulkLoad = bulkLoad;
	}


}
