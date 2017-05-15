package com.marklogic.client.modulesloader.impl;

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

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.helper.LoggingObject;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.modulesloader.ModulesManager;
import com.marklogic.client.modulesloader.xcc.DefaultDocumentFormatGetter;

/**
 * Uses the /v1/documents endpoint in a REST API to load asset modules. This is slower than XccAssetLoader, but it has
 * the advantage of not needing any other permissions other than what's required by the /v1/documents endpoint in the
 * REST API.
 * <p>
 * Note that the DatabaseClient that this class needs must be configured to point to your modules database, not your
 * content database. That's because /v1/documents can only ingest into the database associated with the REST API
 * connection.
 */
public class RestApiAssetLoader extends LoggingObject implements FileVisitor<Path> {

    // Controls what files/directories are processed
    private FileFilter fileFilter = new AssetFileFilter();

    // Default permissions and queryCollections for each module
    private String permissions = "rest-admin,read,rest-admin,update,rest-extension-user,execute";
    private String[] collections;

    private DocumentPermissionsParser documentPermissionsParser = new DefaultDocumentPermissionsParser();
    private FormatGetter formatGetter = new DefaultDocumentFormatGetter();

    // State that is maintained while visiting each asset path. Would need to move this to another class if this
    // class ever needs to be thread-safe.
    private Path currentAssetPath;
    private Path currentRootPath;
    private Set<File> filesLoaded;

    private ModulesManager modulesManager;

    private DocumentWriteSet writeSet;
    private GenericDocumentManager docManager;
    private int writeCount = 0;
    private int batchSize = 100;

    public RestApiAssetLoader(DatabaseClient client) {
        this.docManager = client.newDocumentManager();
    }

    /**
     * For walking one or many paths and loading modules in each of them.
     */
    public Set<File> loadAssets(String... paths) {
        filesLoaded = new HashSet<>();
        try {
            for (String path : paths) {
                if (logger.isDebugEnabled()) {
                    logger.debug(format("Loading assets from path: %s", path));
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
            // Write anything that hasn't been flushed yet
            if (writeSet != null && writeCount > 0) {
                if (logger.isInfoEnabled()) {
                    logger.info("Writing write set");
                }
                docManager.write(writeSet);
            }
            writeCount = 0;
        }
    }

    /**
     * Loads a file into the internally held DocumentWriteSet. If the writeCount is the batchSize or greater, than the
     * writeSet is written.
     *
     * @param uri
     * @param f
     */
    public void loadFile(String uri, File f) {
        if (modulesManager != null && !modulesManager.hasFileBeenModifiedSinceLastInstalled(f)) {
            return;
        }

        DocumentDescriptor descriptor = docManager.newDescriptor(uri);
        descriptor.setFormat(formatGetter.getFormat(f));
        DocumentMetadataHandle metadataHandle = new DocumentMetadataHandle();
        if (this.collections != null) {
            metadataHandle.getCollections().addAll(collections);
        }
        if (this.permissions != null && this.documentPermissionsParser != null) {
            this.documentPermissionsParser.parsePermissions(this.permissions, metadataHandle.getPermissions());
        }
        if (logger.isInfoEnabled()) {
            logger.info(format("Loading module with URI: %s", uri));
        }

        FileHandle fileHandle = new FileHandle(f);
        if (writeSet == null) {
            writeSet = docManager.newWriteSet();
            writeSet.add(descriptor, metadataHandle, fileHandle);
            writeCount++;
        } else if (writeCount >= batchSize) {
            if (logger.isInfoEnabled()) {
                logger.info("Writing write set");
            }
            docManager.write(writeSet);
            writeCount = 0;
        } else {
            writeSet.add(descriptor, metadataHandle, fileHandle);
            writeCount++;
        }
        if (modulesManager != null) {
            modulesManager.saveLastInstalledTimestamp(f, new Date());
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
     * A bit of a hack so that any modules in the samplestack-inspired "ext" directory have "/ext" prepended to their
     * URI.
     *
     * @return
     */
    // protected boolean isNotRootAssetsPath() {
    // return this.currentRootPath != null && this.currentRootPath.toFile().getName().equals("root");
    // }

    @Override
    public FileVisitResult postVisitDirectory(Path path, IOException exception) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path path, IOException exception) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public void setCollections(String[] collections) {
        this.collections = collections;
    }

    public void setFormatGetter(FormatGetter formatGetter) {
        this.formatGetter = formatGetter;
    }

    public void setModulesManager(ModulesManager modulesManager) {
        this.modulesManager = modulesManager;
    }

    public void setFileFilter(FileFilter fileFilter) {
        this.fileFilter = fileFilter;
    }

    public void setDocumentPermissionsParser(DocumentPermissionsParser documentPermissionsParser) {
        this.documentPermissionsParser = documentPermissionsParser;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
}
