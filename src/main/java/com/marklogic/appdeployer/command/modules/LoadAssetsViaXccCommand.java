package com.marklogic.appdeployer.command.modules;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.clientutil.modulesloader.impl.XccAssetLoader;

/**
 * Command for loading assets via XCC from a directory that doesn't fit the REST API structure, where assets are
 * expected to be in a directory named "ext".
 */
public class LoadAssetsViaXccCommand extends AbstractCommand {

    // The list of asset paths to load modules from
    private String[] assetPaths;

    // Default permissions and collections for each module
    private String permissions = "rest-admin,read,rest-admin,update,rest-extension-user,execute";
    private String[] collections;

    public LoadAssetsViaXccCommand(String... assetPaths) {
        this.assetPaths = assetPaths;
        setExecuteSortOrder(SortOrderConstants.LOAD_MODULES_ORDER - 10);
    }

    @Override
    public void execute(CommandContext context) {
        AppConfig config = context.getAppConfig();

        XccAssetLoader loader = new XccAssetLoader();
        loader.setUsername(config.getXdbcUsername());
        loader.setPassword(config.getXdbcPassword());
        loader.setHost(config.getHost());
        loader.setDatabaseName(config.getModulesDatabaseName());
        if (permissions != null) {
            loader.setPermissions(permissions);
        }
        if (collections != null) {
            loader.setCollections(collections);
        }

        loader.loadAssetsViaXcc(assetPaths);
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public void setCollections(String[] collections) {
        this.collections = collections;
    }

}
