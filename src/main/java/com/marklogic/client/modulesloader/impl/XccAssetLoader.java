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
 *     Version 2.11.0 introduced the ability to bulk load modules and to static check each module after it's loaded.
 *     Loading in bulk is now the default. If staticCheck is set to true, then a static check is performed on each
 *     module after it's loaded. By default, this will attempt to do a static check on library modules as well by
 *     extracting the namespace from the module and constructing a main module that imports the library module. This
 *     procedure is not guaranteed to work depending on the contents of the library module - if you run into problems
 *     with it, set staticCheckLibraryModules to false.
 * </p>
 * <p>
 *     In version 2.11.0, catchExceptions was added to affect how errors are logged or thrown during a static check. How
 *     this works differs a bit based on whether modules are loaded in bulk or not. When they are loaded in bulk, they
 *     are all statically checked in one call to MarkLogic. That means that only one exception will be thrown - for the
 *     first module that fails. But when modules are not loaded in bulk, each is statically checked in a separate call
 *     to MarkLogic, and thus when catchExceptions is set to true, each message will be logged.
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

    // Default permissions and collections for each module
    private String permissions = "rest-admin,read,rest-admin,update,rest-extension-user,execute";
    private String[] collections;

    private PermissionsParser permissionsParser = new CommaDelimitedPermissionsParser();
    private DocumentFormatGetter documentFormatGetter = new DefaultDocumentFormatGetter();

    /**
     * When set to true, exceptions thrown while loading modules will be caught and logged, and the
     * module will be updated as having been loaded. This is useful when running a program that watches modules for changes, as it
     * prevents the program from crashing and also from trying to load the module over and over.
     */
    private boolean catchExceptions = false;

    // Whether to load modules in a single request or not
    private boolean bulkLoad = true;

    // If bulkLoad is set to true, keeps track of all the modules to be loaded
    private List<Content> bulkContents;

    // Whether to perform a static check on each loaded module or not
    private boolean staticCheck = false;

    // If staticCheck is true, whether or not to try to static check library modules
    private boolean staticCheckLibraryModules = true;

    // State that is maintained while visiting each asset path. Would need to move this to another class if this
    // class ever needs to be thread-safe.
    private Session activeSession;
    private Path currentAssetPath;
    private Path currentRootPath;
    private Set<File> filesLoaded;

    // Manages when modules were last loaded
    private ModulesManager modulesManager;

    private ModuleTokenReplacer moduleTokenReplacer;

    /**
     * For walking one or many paths and loading modules in each of them.
     */
    public Set<File> loadAssetsViaXcc(String... paths) {
        initializeActiveSession();
        filesLoaded = new HashSet<>();
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
                    if (bulkLoad) {
                        logger.info("Loading bulk contents");
                        activeSession.insertContent(bulkContents.toArray(new Content[]{}));
                        logger.info("Finished loading bulk contents");
                        if (staticCheck) {
                            performBulkStaticCheck(bulkContents);
                        }
                        bulkContents.clear();
                    }
                } catch (Exception ie) {
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
            loadFile(uri, path.toFile());
            filesLoaded.add(path.toFile());
        }

        return FileVisitResult.CONTINUE;
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
        } catch (RequestException re) {
            throw new RuntimeException("Unable to insert content at URI: " + uri + "; cause: " + re.getMessage(), re);
        }

		if (!bulkLoad && staticCheck && moduleCanBeReadAsString(options.getFormat())) {
            staticallyCheckModule(uri);
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

	/**
     * Statically checks the module at the given URI. Includes support for evaluating a library module by trying to
     * extract its namespace and then using xdmp:eval to evaluate a module that imports the library module. If this
     * fails to extract its namespace, an error will be reported just like if the module itself has an error in it.
     *
     * This also uses the checkExceptions property - if it's set to true, then a warning will be logged but no
     * exception will be thrown.
     *
     * @param uri
     */
    protected void staticallyCheckModule(String uri) {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Performing static check on module at URI: " + uri);
            }
            String xquery = "let $uri := '" + uri + "' return " + buildXqueryForStaticallyCheckingModule();
            activeSession.submitRequest(activeSession.newAdhocQuery(xquery));
        } catch (RequestException re) {
            String message = "Static check failed for module at URI: " + uri + "; cause: " + re.getMessage();
            if (catchExceptions) {
                logger.warn(message, re);
            } else {
                throw new RuntimeException(message, re);
            }
        }
    }

	/**
     * Iterate through each Content - which is presumed to have been loaded already - and run an xdmp:invoke on it to
     * static check it. For a library module, an attempt will be made to extract its namespace and evaluate it via an
     * xdmp:eval call.
     */
    protected void performBulkStaticCheck(List<Content> contents) {
        if (contents == null) {
            return;
        }
        String xquery = "let $uris := (";
        for (Content c : contents) {
            String uri = c.getUri();
            if (!xquery.endsWith("(")) {
                xquery += ",";
            }
            xquery += "'" + uri + "'";
        }
        xquery += ") for $uri in $uris return " + buildXqueryForStaticallyCheckingModule();
        try {
            logger.info("Static checking all loaded modules");
            activeSession.submitRequest(activeSession.newAdhocQuery(xquery));
            logger.info("Finished static checking all loaded modules");
        } catch (RequestException re) {
            String message = "Bulk static check failure, cause: " + re.getMessage();
            if (catchExceptions) {
                logger.warn(message, re);
            }
            else {
                throw new RuntimeException(message, re);
            }
        }
    }

    /**
     * Assumes that there's already a variable in XQuery named "uri" in scope. If the module is a library module, an
     * attempt is made to extract its namespace and import it in a statement passed to xdmp:eval. If an error occurs
     * in construct that statement, it cannot be distinguished from an error in the actual module. To turn this behavior
     * off, set "staticCheckLibraryModules" to false.
     *
     * @return
     */
    protected String buildXqueryForStaticallyCheckingModule() {
        String xquery =
            "try { xdmp:invoke($uri, (), <options xmlns='xdmp:eval'><static-check>true</static-check></options>) } " +
            "catch ($e) { " +
            "if ($e/*:code = 'XDMP-NOEXECUTE') then () " +
            "else if ($e/*:code = 'XDMP-EVALLIBMOD') then ";
        if (staticCheckLibraryModules) {
            xquery +=
                "  let $doc := xdmp:eval('declare variable $URI external; fn:doc($URI)', (xs:QName('URI'), $uri), <options xmlns='xdmp:eval'><database>{xdmp:modules-database()}</database></options>) " +
                "  let $line := fn:tokenize($doc, '\n')[fn:contains(., 'module namespace')][1] " +
                "  let $ns := fn:tokenize($line, '=')[2] " +
                "  let $ns := fn:replace($ns, ';', '') " +
                "  let $ns := fn:replace($ns, \"'\", \"\") " +
                "  let $ns := fn:normalize-space(fn:replace($ns, '\"', '')) " +
                "  let $xquery := fn:concat('import module namespace ns = \"', $ns, '\" at \"', $uri, '\"; ()')" +
                "  return xdmp:eval($xquery, (), <options xmlns='xdmp:eval'><static-check>true</static-check></options>) ";
        }
        else {
            xquery += " xdmp:log('ignoring lib mod: ' || $uri) ";
        }
        return xquery + " else xdmp:rethrow() }";
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

    public void setStaticCheck(boolean staticCheck) {
        this.staticCheck = staticCheck;
    }

    public void setBulkLoad(boolean bulkLoad) {
        this.bulkLoad = bulkLoad;
    }

    public void setStaticCheckLibraryModules(boolean staticCheckLibraryModules) {
        this.staticCheckLibraryModules = staticCheckLibraryModules;
    }

    public void setCatchExceptions(boolean catchExceptions) {
        this.catchExceptions = catchExceptions;
    }
}
