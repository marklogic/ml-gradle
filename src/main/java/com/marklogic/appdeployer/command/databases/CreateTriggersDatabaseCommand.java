package com.marklogic.appdeployer.command.databases;

import java.io.File;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.appdeployer.command.UndoableCommand;
import com.marklogic.rest.mgmt.ManageClient;
import com.marklogic.rest.mgmt.databases.DatabaseManager;
import com.marklogic.rest.mgmt.forests.ForestManager;
import com.marklogic.rest.mgmt.hosts.HostManager;

public class CreateTriggersDatabaseCommand extends AbstractCommand implements UndoableCommand {

    public CreateTriggersDatabaseCommand() {
        setExecuteSortOrder(SortOrderConstants.CREATE_TRIGGERS_DATABASE_ORDER);
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
        AppConfig config = context.getAppConfig();
        File f = config.getConfigDir().getTriggersDatabaseFile();
        if (f.exists()) {
            logger.info("Creating triggers database based on file at: " + f.getAbsolutePath());
            String payload = copyFileToString(f);
            payload = tokenReplacer.replaceTokens(payload, config, false);
            createTriggersDatabase(payload, context);
        } else if (config.isCreateTriggerDatabase()) {
            logger.info("Creating triggers database because AppConfig property is set to true");
            createTriggersDatabase(buildDefaultTriggersDatabasePayload(config), context);
        } else {
            logger.info("Not creating a triggers database, no file found at: " + f.getAbsolutePath());
        }
    }

    protected void createTriggersDatabase(String payload, CommandContext context) {
        DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());
        dbMgr.save(payload);
        createAndAttachForestOnEachHost(context.getAppConfig().getTriggersDatabaseName(), context.getManageClient());
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
        AppConfig config = context.getAppConfig();
        File f = config.getConfigDir().getTriggersDatabaseFile();
        if (f.exists()) {
            String payload = copyFileToString(f);
            payload = tokenReplacer.replaceTokens(payload, context.getAppConfig(), false);
            new DatabaseManager(context.getManageClient()).delete(payload);
        } else if (config.isCreateTriggerDatabase()) {
            new DatabaseManager(context.getManageClient()).delete(buildDefaultTriggersDatabasePayload(config));
        }
    }

    protected String buildDefaultTriggersDatabasePayload(AppConfig config) {
        return format("{\"database-name\": \"%s\"}", config.getTriggersDatabaseName());
    }
}
