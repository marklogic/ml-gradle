package com.marklogic.appdeployer.app.plugin;

import java.io.File;

import com.marklogic.appdeployer.app.AppPluginContext;
import com.marklogic.rest.mgmt.ManageClient;
import com.marklogic.rest.mgmt.databases.DatabaseManager;
import com.marklogic.rest.mgmt.forests.ForestManager;
import com.marklogic.rest.mgmt.hosts.HostManager;

public class TriggersDatabasePlugin extends AbstractPlugin {

    @Override
    public Integer getSortOrderOnCreate() {
        return 500;
    }

    @Override
    public void onCreate(AppPluginContext context) {
        File f = context.getConfigDir().getTriggersDatabaseFile();
        if (f.exists()) {
            DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());

            String dbName = context.getAppConfig().getTriggersDatabaseName();
            String payload = copyFileToString(f);
            payload = replaceConfigTokens(payload, context.getAppConfig(), false);
            dbMgr.createDatabase(dbName, payload);

            createAndAttachForestOnEachHost(dbName, context.getManageClient());

            dbMgr.assignTriggersDatabase(context.getAppConfig().getContentDatabaseName(), dbName);
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
    public void onDelete(AppPluginContext context) {
        DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());
        dbMgr.deleteDatabase(context.getAppConfig().getTriggersDatabaseName());
    }

}
