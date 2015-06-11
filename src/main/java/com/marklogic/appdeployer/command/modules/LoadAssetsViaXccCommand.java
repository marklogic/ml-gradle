package com.marklogic.appdeployer.command.modules;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.CommandContext;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.xcc.Content;
import com.marklogic.xcc.ContentCreateOptions;
import com.marklogic.xcc.ContentFactory;
import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.ContentSourceFactory;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;

/**
 * This command is for loading assets from any directory in a project via XCC. It's useful for when you have dozens of
 * assets or more, as using the /v1/ext REST API endpoint is often too slow in such cases.
 */
public class LoadAssetsViaXccCommand extends AbstractCommand implements FileVisitor<Path> {

    // XCC connection info
    private String username;
    private String password;
    private String host;
    private Integer port = 8000;
    private String databaseName;

    // The list of asset paths to load modules from
    private List<Path> assetPaths;

    // Default permissions and collections for each module
    private String permissions = "rest-admin,read,rest-admin,update,rest-extension-user,execute";
    private String[] collections;

    private PermissionsParser permissionsParser = new CommaDelimitedPermissionsParser();
    private DocumentFormatGetter documentFormatGetter = new DefaultDocumentFormatGetter();

    // State that is maintained while visiting each asset path. Would need to move this to another class if this
    // command ever needs to be thread-safe.
    private Session activeSession;
    private Path currentAssetPath;

    public LoadAssetsViaXccCommand(String... paths) {
        assetPaths = new ArrayList<Path>();
        for (String path : paths) {
            assetPaths.add(Paths.get(path));
        }
    }

    @Override
    public Integer getExecuteSortOrder() {
        return SortOrderConstants.LOAD_MODULES_ORDER - 10;
    }

    @Override
    public void execute(CommandContext context) {
        AppConfig config = context.getAppConfig();

        ContentSource cs = ContentSourceFactory.newContentSource(host != null ? host : config.getHost(), this.port,
                username != null ? username : config.getUsername(), password != null ? password : config.getPassword(),
                databaseName != null ? databaseName : config.getModulesDatabaseName());
        activeSession = cs.newSession();

        try {
            for (Path path : assetPaths) {
                this.currentAssetPath = path;
                try {
                    Files.walkFileTree(path, this);
                } catch (IOException ie) {
                    throw new RuntimeException("Error while walking assets file tree: " + ie.getMessage(), ie);
                }
            }
        } finally {
            activeSession.close();
            activeSession = null;
        }
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) throws IOException {
        if (attributes.isRegularFile()) {
            Path relPath = currentAssetPath.relativize(path);
            String uri = "/" + relPath.toString().replace("\\", "/");

            ContentCreateOptions options = new ContentCreateOptions();
            options.setFormat(documentFormatGetter.getDocumentFormat(relPath.toFile()));
            options.setPermissions(permissionsParser.parsePermissions(this.permissions));
            if (this.collections != null) {
                options.setCollections(collections);
            }

            logger.info("Inserting module into URI: " + uri);
            Content content = ContentFactory.newContent(uri, path.toFile(), options);
            try {
                activeSession.insertContent(content);
            } catch (RequestException re) {
                throw new RuntimeException("Unable to insert content at URI: " + uri + "; cause: " + re.getMessage(),
                        re);
            }
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path arg0, IOException arg1) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path arg0, BasicFileAttributes arg1) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path arg0, IOException arg1) throws IOException {
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

}
