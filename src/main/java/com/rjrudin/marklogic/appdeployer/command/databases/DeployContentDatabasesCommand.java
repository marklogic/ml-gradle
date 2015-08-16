package com.rjrudin.marklogic.appdeployer.command.databases;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.rjrudin.marklogic.appdeployer.AppConfig;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.mgmt.SaveReceipt;
import com.rjrudin.marklogic.mgmt.databases.DatabaseManager;
import com.rjrudin.marklogic.rest.util.JsonNodeUtil;

/**
 * For ease of use, this command handles creating forests the the content database, either based on a file in the
 * forests directory, or based on the default payload in the CreateForestsCommand class. This allows a developer to only
 * have to define a content database file and not have to define a forest file as well. Note that if no content database
 * file exists, then this command won't do anything, and it's then expected that a content database is created via the
 * command for creating a REST API instance.
 */
public class DeployContentDatabasesCommand extends DeployDatabaseCommand {

    public DeployContentDatabasesCommand() {
        setExecuteSortOrder(SortOrderConstants.CREATE_CONTENT_DATABASES);
        setUndoSortOrder(SortOrderConstants.DELETE_CONTENT_DATABASES);

        // Same default as /v1/rest-apis
        setForestsPerHost(3);
        setForestFilename("content-forest.json");
    }

    /**
     * Calls the parent method and then adds support for a test content database.
     */
    @Override
    public void execute(CommandContext context) {
        super.execute(context);

        AppConfig appConfig = context.getAppConfig();
        if (appConfig.isTestPortSet()) {
            String payload = getPayload(context);
            if (payload != null) {
                DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());
                String json = tokenReplacer.replaceTokens(payload, appConfig, true);
                SaveReceipt receipt = dbMgr.save(json);
                createForestsIfDatabaseWasJustCreated(receipt, context);
            }
        }
    }

    /**
     * Just because there's not a content database file doesn't mean that one wasn't created via the command for
     * creating a REST API server. If the REST API server command didn't delete the content database, we'd still want
     * this command to attempt to do so in the event that no content database files exist.
     */
    @Override
    public void undo(CommandContext context) {
        AppConfig appConfig = context.getAppConfig();
        JsonNode node = mergeContentDatabaseFiles(appConfig);
        if (node != null) {
            logger.info("No content database files found, so not deleting content databases");
            String payload = node.toString();
            String json = tokenReplacer.replaceTokens(payload, appConfig, false);

            DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());
            dbMgr.setForestDelete(getForestDelete());
            dbMgr.delete(json);

            if (appConfig.isTestPortSet()) {
                json = tokenReplacer.replaceTokens(payload, appConfig, true);
                dbMgr.delete(json);
            }
        } else {
            // Try to delete the content database if it exists
            DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());
            dbMgr.setForestDelete(getForestDelete());
            dbMgr.deleteByName(appConfig.getContentDatabaseName());

            if (appConfig.isTestPortSet()) {
                dbMgr.deleteByName(appConfig.getContentDatabaseName());
            }
        }
    }

    @Override
    protected String getPayload(CommandContext context) {
        JsonNode node = mergeContentDatabaseFiles(context.getAppConfig());
        if (node == null) {
            logger.info("No content database files found, so not processing");
            return null;
        }
        return node.toString();
    }

    protected JsonNode mergeContentDatabaseFiles(AppConfig appConfig) {
        List<File> files = appConfig.getConfigDir().getContentDatabaseFiles();
        if (logger.isInfoEnabled()) {
            logger.info("Merging JSON files at locations: " + files);
        }
        return JsonNodeUtil.mergeJsonFiles(files);
    }

}
