package com.marklogic.appdeployer.command.modules;

import java.io.File;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.clientutil.modulesloader.ModulesLoader;
import com.marklogic.clientutil.modulesloader.impl.DefaultModulesLoader;
import com.marklogic.clientutil.modulesloader.impl.TestServerModulesFinder;
import com.marklogic.clientutil.modulesloader.impl.XccAssetLoader;

/**
 * By default, uses XCC to load modules, as that's normally much faster than using the /v1/ext REST API endpoint.
 */
public class LoadModulesCommand extends AbstractCommand {

    private ModulesLoader modulesLoader;

    // As defined by the REST API
    private String defaultAssetRolesAndCapabilities = "rest-admin,read,rest-admin,update,rest-extension-user,execute";
    private String customAssetRolesAndCapabilities;

    private String username;
    private String password;
    
    public LoadModulesCommand() {
        setExecuteSortOrder(SortOrderConstants.LOAD_MODULES_ORDER);
    }

    @Override
    public void execute(CommandContext context) {
        loadModulesIntoMainServer(context);

        if (context.getAppConfig().isTestPortSet()) {
            loadModulesIntoTestServer(context);
        }
    }

    protected void loadModulesIntoMainServer(CommandContext context) {
        if (modulesLoader == null) {
            DefaultModulesLoader l = new DefaultModulesLoader();
            l.setXccAssetLoader(newXccAssetLoader(context));
            this.modulesLoader = l;
        }

        AppConfig config = context.getAppConfig();

        DatabaseClient client = DatabaseClientFactory.newClient(config.getHost(), config.getRestPort(),
                config.getRestAdminUsername(), config.getRestAdminPassword(), config.getAuthentication());

        for (String modulesPath : config.getModulePaths()) {
            logger.info("Loading modules from dir: " + modulesPath);
            modulesLoader.loadModules(new File(modulesPath), client);
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
                config.getRestAdminUsername(), config.getRestAdminPassword(), config.getAuthentication());

        DefaultModulesLoader l = new DefaultModulesLoader();
        l.setModulesFinder(new TestServerModulesFinder());
        l.setModulesManager(null);

        for (String modulesPath : config.getModulePaths()) {
            logger.info("Loading modules into test server from dir: " + modulesPath);
            l.loadModules(new File(modulesPath), client);
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

    public void setModulesLoader(ModulesLoader modulesLoader) {
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
