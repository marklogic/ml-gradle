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

public class CreateSchemasDatabaseCommand extends AbstractCommand implements UndoableCommand {

    public CreateSchemasDatabaseCommand() {
        setExecuteSortOrder(SortOrderConstants.CREATE_SCHEMAS_DATABASE);
    }

    /**
     * Have to first delete the REST API server and its content database, then delete the schemas database.
     */
    @Override
    public Integer getUndoSortOrder() {
        return SortOrderConstants.CREATE_REST_API_SERVERS + 10;
    }

    @Override
    public void execute(CommandContext context) {
        AppConfig config = context.getAppConfig();
        File f = config.getConfigDir().getSchemasDatabaseFile();
        if (f.exists()) {
            logger.info("Creating schemas database based on file at: " + f.getAbsolutePath());
            String payload = copyFileToString(f);
            payload = tokenReplacer.replaceTokens(payload, config, false);
            createSchemasDatabase(payload, context);
        } else {
            logger.info("Not creating a schemas database, no file found at: " + f.getAbsolutePath());
        }
    }

    protected void createSchemasDatabase(String payload, CommandContext context) {
        DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());
        dbMgr.save(payload);
        createAndAttachForestOnEachHost(context.getAppConfig().getSchemasDatabaseName(), context.getManageClient());
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
        File f = config.getConfigDir().getSchemasDatabaseFile();
        if (f.exists()) {
            String payload = copyFileToString(f);
            payload = tokenReplacer.replaceTokens(payload, context.getAppConfig(), false);
            new DatabaseManager(context.getManageClient()).delete(payload);
        }
    }
}
