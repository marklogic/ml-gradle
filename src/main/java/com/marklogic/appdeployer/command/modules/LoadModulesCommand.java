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
import com.marklogic.clientutil.modulesloader.impl.XccAssetLoader;

/**
 * By default, uses XCC to load modules, as that's normally much faster than using the /v1/ext REST API endpoint.
 */
public class LoadModulesCommand extends AbstractCommand {

    private ModulesLoader modulesLoader;

    // As defined by the REST API
    private String defaultAssetRolesAndCapabilities = "rest-admin,read,rest-admin,update,rest-extension-user,execute";
    private String customAssetRolesAndCapabilities;

    public LoadModulesCommand() {
        setExecuteSortOrder(SortOrderConstants.LOAD_MODULES_ORDER);
    }

    @Override
    public void execute(CommandContext context) {
        if (modulesLoader == null) {
            DefaultModulesLoader l = new DefaultModulesLoader();
            l.setXccAssetLoader(newXccAssetLoader(context));
            this.modulesLoader = l;
        }

        AppConfig config = context.getAppConfig();

        DatabaseClient client = DatabaseClientFactory.newClient(config.getHost(), config.getRestPort(),
                config.getUsername(), config.getPassword(), config.getAuthentication());

        for (String modulesPath : config.getModulePaths()) {
            logger.info("Loading modules from dir: " + modulesPath);
            modulesLoader.loadModules(new File(modulesPath), client);
        }
    }

    protected XccAssetLoader newXccAssetLoader(CommandContext context) {
        XccAssetLoader l = new XccAssetLoader();
        AppConfig config = context.getAppConfig();
        l.setHost(config.getHost());
        l.setUsername(config.getXdbcUsername());
        l.setPassword(config.getXdbcPassword());
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
}
