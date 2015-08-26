package com.rjrudin.marklogic.appdeployer.command.modules;

import java.io.File;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.rjrudin.marklogic.appdeployer.AppConfig;
import com.rjrudin.marklogic.appdeployer.command.AbstractCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.modulesloader.impl.DefaultModulesLoader;
import com.rjrudin.marklogic.modulesloader.impl.TestServerModulesFinder;
import com.rjrudin.marklogic.modulesloader.impl.XccAssetLoader;

/**
 * By default, uses XCC to load modules, as that's normally much faster than using the /v1/ext REST API endpoint.
 */
public class LoadModulesCommand extends AbstractCommand {

    private DefaultModulesLoader modulesLoader;

    // As defined by the REST API
    private String defaultAssetRolesAndCapabilities = "rest-admin,read,rest-admin,update,rest-extension-user,execute";
    private String customAssetRolesAndCapabilities;

    private String username;
    private String password;

    public LoadModulesCommand() {
        setExecuteSortOrder(SortOrderConstants.LOAD_MODULES);
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
            this.modulesLoader = new DefaultModulesLoader(newXccAssetLoader(context));
        }

        AppConfig config = context.getAppConfig();

        DatabaseClient client = DatabaseClientFactory.newClient(config.getHost(), config.getRestPort(),
                config.getRestAdminUsername(), config.getRestAdminPassword(), config.getRestAuthentication(),
                config.getRestSslContext(), config.getRestSslHostnameVerifier());

        try {
            this.modulesLoader.setModulesFinder(new AssetModulesFinder());
            for (String modulesPath : config.getModulePaths()) {
                logger.info("Loading asset modules from dir: " + modulesPath);
                modulesLoader.loadModules(new File(modulesPath), client);
            }

            this.modulesLoader.setModulesFinder(new AllButAssetsModulesFinder());
            for (String modulesPath : config.getModulePaths()) {
                logger.info("Loading all non-asset modules from dir: " + modulesPath);
                modulesLoader.loadModules(new File(modulesPath), client);
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

        try {
            // Don't need an XccAssetLoader here, as only options/properties are loaded for the test server
            DefaultModulesLoader l = new DefaultModulesLoader(null);
            l.setModulesFinder(new TestServerModulesFinder());
            l.setModulesManager(null);

            for (String modulesPath : config.getModulePaths()) {
                logger.info("Loading modules into test server from dir: " + modulesPath);
                l.loadModules(new File(modulesPath), client);
            }
        } finally {
            client.release();
        }
    }

    protected XccAssetLoader newXccAssetLoader(CommandContext context) {
        XccAssetLoader l = new XccAssetLoader();
        AppConfig config = context.getAppConfig();
        l.setHost(config.getHost());
        l.setUsername(username != null ? username : config.getRestAdminUsername());
        l.setPassword(password != null ? password : config.getRestAdminPassword());
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
        return l;
    }

    public void setModulesLoader(DefaultModulesLoader modulesLoader) {
        this.modulesLoader = modulesLoader;
    }

    public void setCustomAssetRolesAndCapabilities(String customAssetRolesAndCapabilities) {
        this.customAssetRolesAndCapabilities = customAssetRolesAndCapabilities;
    }

    public void setDefaultAssetRolesAndCapabilities(String defaultAssetRolesAndCapabilities) {
        this.defaultAssetRolesAndCapabilities = defaultAssetRolesAndCapabilities;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
