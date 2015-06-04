package com.marklogic.appdeployer.command.databases;

import java.io.File;

import com.marklogic.appdeployer.CommandContext;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.rest.mgmt.ManageClient;
import com.marklogic.rest.mgmt.databases.DatabaseManager;
import com.marklogic.rest.mgmt.forests.ForestManager;
import com.marklogic.rest.mgmt.hosts.HostManager;

public class CreateTriggersDatabasePlugin extends AbstractCommand {

    @Override
    public Integer getExecuteSortOrder() {
        return SortOrderConstants.CREATE_TRIGGERS_DATABASE_ORDER;
    }

    @Override
    public void execute(CommandContext context) {
        File f = context.getAppConfig().getConfigDir().getTriggersDatabaseFile();
        if (f.exists()) {
            DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());

            String dbName = context.getAppConfig().getTriggersDatabaseName();
            String payload = copyFileToString(f);
            payload = tokenReplacer.replaceTokens(payload, context.getAppConfig(), false);
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
    public void undo(CommandContext context) {
        DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());
        dbMgr.deleteDatabase(context.getAppConfig().getTriggersDatabaseName());
    }

}
