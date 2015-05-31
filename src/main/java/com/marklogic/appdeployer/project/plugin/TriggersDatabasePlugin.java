package com.marklogic.appdeployer.project.plugin;

import java.io.File;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.mgmt.ManageClient;
import com.marklogic.appdeployer.mgmt.databases.DatabaseManager;
import com.marklogic.appdeployer.mgmt.forests.ForestManager;
import com.marklogic.appdeployer.mgmt.hosts.HostManager;
import com.marklogic.appdeployer.project.AbstractPlugin;
import com.marklogic.appdeployer.project.ConfigDir;

public class TriggersDatabasePlugin extends AbstractPlugin {

    @Override
    public Integer getSortOrderOnCreate() {
        return 500;
    }

    @Override
    public void onCreate(AppConfig appConfig, ConfigDir configDir, ManageClient manageClient) {
        File f = configDir.getTriggersDatabaseFile();
        if (f.exists()) {
            DatabaseManager dbMgr = new DatabaseManager(manageClient);

            String dbName = appConfig.getTriggersDatabaseName();
            String payload = copyFileToString(f);
            payload = replaceConfigTokens(payload, appConfig, false);
            dbMgr.createDatabase(dbName, payload);

            createAndAttachForestOnEachHost(dbName, manageClient);

            dbMgr.assignTriggersDatabase(appConfig.getContentDatabaseName(), dbName);
        } else {
            logger.info("Not creating a triggers database, no file found at: " + f.getAbsolutePath());
        }
    }

    public void createAndAttachForestOnEachHost(String dbName, ManageClient client) {
        ForestManager fmgr = new ForestManager(client);
        String forestName = dbName + "-1";
        for (String hostName : new HostManager(client).getHostNames()) {
            fmgr.createForestWithName(forestName, hostName);
            fmgr.attachForest(forestName, dbName);
        }
    }

    @Override
    public void onDelete(AppConfig appConfig, ConfigDir configDir, ManageClient manageClient) {
        DatabaseManager dbMgr = new DatabaseManager(manageClient);
        dbMgr.deleteDatabase(appConfig.getTriggersDatabaseName());
    }

}
