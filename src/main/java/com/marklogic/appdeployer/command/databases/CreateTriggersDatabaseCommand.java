package com.marklogic.appdeployer.command.databases;

import java.io.File;

import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.appdeployer.command.UndoableCommand;
import com.marklogic.rest.mgmt.ManageClient;
import com.marklogic.rest.mgmt.databases.DatabaseManager;
import com.marklogic.rest.mgmt.forests.ForestManager;
import com.marklogic.rest.mgmt.hosts.HostManager;

public class CreateTriggersDatabaseCommand extends AbstractCommand implements UndoableCommand {

    @Override
    public Integer getExecuteSortOrder() {
        return SortOrderConstants.CREATE_TRIGGERS_DATABASE_ORDER;
    }

    /**
     * Have to first delete the REST API server and its content database, then delete the trigger database.
     */
    @Override
    public Integer getUndoSortOrder() {
        return SortOrderConstants.CREATE_REST_API_SERVERS_ORDER + 10;
    }

    @Override
    public void execute(CommandContext context) {
        File f = context.getAppConfig().getConfigDir().getTriggersDatabaseFile();
        if (f.exists()) {
            DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());

            String dbName = context.getAppConfig().getTriggersDatabaseName();
            String payload = copyFileToString(f);
            payload = tokenReplacer.replaceTokens(payload, context.getAppConfig(), false);
            dbMgr.save(payload);

            createAndAttachForestOnEachHost(dbName, context.getManageClient());
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
        File f = context.getAppConfig().getConfigDir().getTriggersDatabaseFile();
        if (f.exists()) {
            DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());
            String payload = copyFileToString(f);
            payload = tokenReplacer.replaceTokens(payload, context.getAppConfig(), false);
            dbMgr.delete(payload);
        }
    }

}
