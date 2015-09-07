package com.rjrudin.marklogic.modulesloader.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.marklogic.xcc.Content;
import com.marklogic.xcc.ContentCreateOptions;
import com.marklogic.xcc.ContentFactory;
import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.ContentSourceFactory;
import com.marklogic.xcc.SecurityOptions;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;
import com.rjrudin.marklogic.client.LoggingObject;
import com.rjrudin.marklogic.modulesloader.ModulesManager;
import com.rjrudin.marklogic.modulesloader.xcc.CommaDelimitedPermissionsParser;
import com.rjrudin.marklogic.modulesloader.xcc.DefaultDocumentFormatGetter;
import com.rjrudin.marklogic.modulesloader.xcc.DocumentFormatGetter;
import com.rjrudin.marklogic.modulesloader.xcc.PermissionsParser;

/**
 * Handles loading assets - as defined by the REST API, which are typically under the /ext directory - via XCC.
 * Currently not a threadsafe class - in order to make it threadsafe, would need to move the impl of FileVisitor to a
 * separate class.
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

    // Default permissions and collections for each module
    private String permissions = "rest-admin,read,rest-admin,update,rest-extension-user,execute";
    private String[] collections;

    private PermissionsParser permissionsParser = new CommaDelimitedPermissionsParser();
    private DocumentFormatGetter documentFormatGetter = new DefaultDocumentFormatGetter();

    // State that is maintained while visiting each asset path. Would need to move this to another class if this
    // class ever needs to be thread-safe.
    private Session activeSession;
    private Path currentAssetPath;
    private Path currentRootPath;
    private Set<File> filesLoaded;

    private ModulesManager modulesManager;

    /**
     * For walking one or many paths and loading modules in each of them.
     */
    public Set<File> loadAssetsViaXcc(String... paths) {
        initializeActiveSession();
        filesLoaded = new HashSet<>();
        try {
            for (String path : paths) {
                if (logger.isInfoEnabled()) {
                    logger.info(format("Loading assets from path: %s", path));
                }
                this.currentAssetPath = Paths.get(path);
                this.currentRootPath = this.currentAssetPath;
                try {
                    Files.walkFileTree(this.currentAssetPath, this);
                } catch (IOException ie) {
                    throw new RuntimeException(format("Error while walking assets file tree: %s", ie.getMessage()), ie);
                }
            }
            return filesLoaded;
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
            if (logger.isDebugEnabled()) {
                logger.debug("Visiting directory: " + path);
            }
            return FileVisitResult.CONTINUE;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Skipping directory: " + path);
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
            if (isCurrentRootPathForRestApiAssets()) {
                uri = "/ext" + uri;
            }
            loadFile(uri, path.toFile());
            filesLoaded.add(path.toFile());
        }

        return FileVisitResult.CONTINUE;
    }

    /**
     * A bit of a hack so that any modules in the samplestack-inspired "ext" directory have "/ext" prepended to their
     * URI.
     * 
     * @return
     */
    protected boolean isCurrentRootPathForRestApiAssets() {
        return this.currentRootPath != null && this.currentRootPath.toFile().getName().equals("ext");
    }

    /**
     * Does the actual work of loading a file into the modules database via XCC.
     * 
     * @param uri
     * @param f
     */
    protected void loadFile(String uri, File f) {
        if (modulesManager != null && !modulesManager.hasFileBeenModifiedSinceLastInstalled(f)) {
            return;
        }

        ContentCreateOptions options = new ContentCreateOptions();
        options.setFormat(documentFormatGetter.getDocumentFormat(f));
        options.setPermissions(permissionsParser.parsePermissions(this.permissions));
        if (this.collections != null) {
            options.setCollections(collections);
        }

        if (logger.isInfoEnabled()) {
            logger.info(format("Inserting module with URI: %s", uri));
        }

        Content content = ContentFactory.newContent(uri, f, options);
        try {
            activeSession.insertContent(content);
            if (modulesManager != null) {
                modulesManager.saveLastInstalledTimestamp(f, new Date());
            }
        } catch (RequestException re) {
            throw new RuntimeException("Unable to insert content at URI: " + uri + "; cause: " + re.getMessage(), re);
        }
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
}
