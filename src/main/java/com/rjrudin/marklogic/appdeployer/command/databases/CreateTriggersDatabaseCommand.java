package com.rjrudin.marklogic.appdeployer.command.databases;

import java.io.File;

import com.rjrudin.marklogic.appdeployer.AppConfig;
import com.rjrudin.marklogic.appdeployer.command.AbstractCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.appdeployer.command.UndoableCommand;
import com.rjrudin.marklogic.mgmt.ManageClient;
import com.rjrudin.marklogic.mgmt.databases.DatabaseManager;
import com.rjrudin.marklogic.mgmt.forests.ForestManager;
import com.rjrudin.marklogic.mgmt.hosts.HostManager;

public class CreateTriggersDatabaseCommand extends AbstractCommand implements UndoableCommand {

    public CreateTriggersDatabaseCommand() {
        setExecuteSortOrder(SortOrderConstants.CREATE_TRIGGERS_DATABASE);
    }

    @Override
    public Integer getUndoSortOrder() {
        return SortOrderConstants.DELETE_TRIGGERS_DATABASE;
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
        } else if (config.isCreateTriggersDatabase()) {
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
        } else if (config.isCreateTriggersDatabase()) {
            new DatabaseManager(context.getManageClient()).deleteByIdField(config.getTriggersDatabaseName());
        }
    }

    protected String buildDefaultTriggersDatabasePayload(AppConfig config) {
        return format("{\"database-name\": \"%s\"}", config.getTriggersDatabaseName());
    }
}
