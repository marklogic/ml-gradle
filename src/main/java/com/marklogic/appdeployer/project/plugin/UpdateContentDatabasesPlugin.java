package com.marklogic.appdeployer.project.plugin;

import java.io.File;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.app.AbstractPlugin;
import com.marklogic.appdeployer.app.ConfigDir;
import com.marklogic.appdeployer.mgmt.ManageClient;
import com.marklogic.appdeployer.mgmt.databases.DatabaseManager;

public class UpdateContentDatabasesPlugin extends AbstractPlugin {

    @Override
    public Integer getSortOrderOnCreate() {
        return 200;
    }

    /**
     * Check for content-database.json; if it exists, then PUT it against the existing content database.
     */
    @Override
    public void onCreate(AppConfig appConfig, ConfigDir configDir, ManageClient manageClient) {
        File f = configDir.getContentDatabaseFile();
        if (f.exists()) {
            DatabaseManager dbMgr = new DatabaseManager(manageClient);

            String payload = copyFileToString(f);

            String json = replaceConfigTokens(payload, appConfig, false);
            dbMgr.updateDatabase(appConfig.getContentDatabaseName(), json);

            if (appConfig.isTestPortSet()) {
                json = replaceConfigTokens(payload, appConfig, true);
                dbMgr.updateDatabase(appConfig.getTestContentDatabaseName(), json);
            }
        } else {
            logger.info(format("No content database file found at %s, so not updating the content database",
                    f.getAbsolutePath()));
        }
    }

    @Override
    public void onDelete(AppConfig appConfig, ConfigDir configDir, ManageClient manageClient) {
    }

}
