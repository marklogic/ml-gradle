package com.rjrudin.marklogic.appdeployer.command.modules;

import java.io.File;
import java.io.FileFilter;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.rjrudin.marklogic.appdeployer.AppConfig;
import com.rjrudin.marklogic.appdeployer.command.AbstractCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.modulesloader.ModulesLoader;
import com.rjrudin.marklogic.modulesloader.impl.DefaultModulesLoader;
import com.rjrudin.marklogic.modulesloader.impl.TestServerModulesFinder;
import com.rjrudin.marklogic.modulesloader.impl.XccAssetLoader;

/**
 * Command for loading modules via an instance of DefaultModulesLoader, which depends on an instance of XccAssetLoader -
 * these are all in the ml-javaclient-util library.
 */
public class LoadModulesCommand extends AbstractCommand {

    private ModulesLoader modulesLoader;
    private FileFilter assetFileFilter;

    // As defined by the REST API
    private String defaultAssetRolesAndCapabilities = "rest-admin,read,rest-admin,update,rest-extension-user,execute";
    private String customAssetRolesAndCapabilities;

    private String xccUsername;
    private String xccPassword;

    public LoadModulesCommand() {
        setExecuteSortOrder(SortOrderConstants.LOAD_MODULES);
    }

    /**
     * Public so that a client can initialize the ModulesLoader and then access it via the getter; this is useful for a
     * tool like ml-gradle, where the ModulesLoader can be reused by multiple tasks.
     * 
     * @param context
     */
    public void initializeDefaultModulesLoader(CommandContext context) {
        logger.info("Initializing instance of DefaultModulesLoader");
        this.modulesLoader = new DefaultModulesLoader(newXccAssetLoader(context));
    }

    @Override
    public void execute(CommandContext context) {
        loadModulesIntoMainServer(context);

        if (context.getAppConfig().isTestPortSet()) {
            loadModulesIntoTestServer(context);
        }
    }

    /**
     * If we have multiple module paths, we want to load via XCC the assets for each first, and then iterate over the
     * paths again and load all the REST API resources. This ensures that if the REST server for loading REST API
     * resources has a custom rewriter, it's guaranteed to be loaded before we try to load any REST API resources.
     * 
     * @param context
     */
    protected void loadModulesIntoMainServer(CommandContext context) {
        if (modulesLoader == null) {
            initializeDefaultModulesLoader(context);
        }

        AppConfig config = context.getAppConfig();
        DatabaseClient client = config.newDatabaseClient();

        try {
            for (String modulesPath : config.getModulePaths()) {
                logger.info("Loading asset modules from dir: " + modulesPath);
                modulesLoader.loadModules(new File(modulesPath), new AssetModulesFinder(), client);
            }

            for (String modulesPath : config.getModulePaths()) {
                logger.info("Loading all non-asset modules from dir: " + modulesPath);
                modulesLoader.loadModules(new File(modulesPath), new AllButAssetsModulesFinder(), client);
            }
        } finally {
            client.release();
        }
    }

    /**
     * We use a customized impl of DefaultModulesLoader here so we can ensure that options are always loaded again into
     * the test server.
     * 
     * @param context
     */
    protected void loadModulesIntoTestServer(CommandContext context) {
        AppConfig config = context.getAppConfig();

        DatabaseClient client = DatabaseClientFactory.newClient(config.getHost(), config.getTestRestPort(),
                config.getRestAdminUsername(), config.getRestAdminPassword(), config.getRestAuthentication(),
                config.getRestSslContext(), config.getRestSslHostnameVerifier());

        ModulesLoader testLoader = buildTestModulesLoader(context);

        try {
            for (String modulesPath : config.getModulePaths()) {
                logger.info("Loading modules into test server from dir: " + modulesPath);
                testLoader.loadModules(new File(modulesPath), new TestServerModulesFinder(), client);
            }
        } finally {
            client.release();
        }
    }

    protected ModulesLoader buildTestModulesLoader(CommandContext context) {
        // Don't need an XccAssetLoader here, as only options/properties are loaded for the test server
        DefaultModulesLoader l = new DefaultModulesLoader(null);
        l.setModulesManager(null);
        return l;
    }

    protected XccAssetLoader newXccAssetLoader(CommandContext context) {
        XccAssetLoader l = new XccAssetLoader();
        AppConfig config = context.getAppConfig();
        l.setHost(config.getHost());
        l.setUsername(xccUsername != null ? xccUsername : config.getRestAdminUsername());
        l.setPassword(xccPassword != null ? xccPassword : config.getRestAdminPassword());
        l.setDatabaseName(config.getModulesDatabaseName());

        String permissions = null;
        if (defaultAssetRolesAndCapabilities != null) {
            permissions = defaultAssetRolesAndCapabilities;
            if (customAssetRolesAndCapabilities != null) {
                permissions += "," + customAssetRolesAndCapabilities;
            }
        } else {
            permissions = customAssetRolesAndCapabilities;
        }

        if (permissions != null) {
            logger.info("Will load asset modules with roles and capabilities of: " + permissions);
            l.setPermissions(permissions);
        }

        if (assetFileFilter != null) {
            l.setFileFilter(assetFileFilter);
        }

        return l;
    }

    public void setModulesLoader(ModulesLoader modulesLoader) {
        this.modulesLoader = modulesLoader;
    }

    public void setCustomAssetRolesAndCapabilities(String customAssetRolesAndCapabilities) {
        this.customAssetRolesAndCapabilities = customAssetRolesAndCapabilities;
    }

    public void setDefaultAssetRolesAndCapabilities(String defaultAssetRolesAndCapabilities) {
        this.defaultAssetRolesAndCapabilities = defaultAssetRolesAndCapabilities;
    }

    public void setXccUsername(String username) {
        this.xccUsername = username;
    }

    public void setXccPassword(String password) {
        this.xccPassword = password;
    }

    public void setAssetFileFilter(FileFilter assetFileFilter) {
        this.assetFileFilter = assetFileFilter;
    }

    public ModulesLoader getModulesLoader() {
        return modulesLoader;
    }
}
