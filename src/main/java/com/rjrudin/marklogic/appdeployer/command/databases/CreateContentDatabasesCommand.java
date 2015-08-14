package com.rjrudin.marklogic.appdeployer.command.databases;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.rjrudin.marklogic.appdeployer.AppConfig;
import com.rjrudin.marklogic.appdeployer.command.AbstractCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.appdeployer.command.UndoableCommand;
import com.rjrudin.marklogic.appdeployer.command.forests.CreateForestsCommand;
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
public class CreateContentDatabasesCommand extends AbstractCommand implements UndoableCommand {

    private String forestDelete = "data";
    // Same default as /v1/rest-apis
    private int forestsPerHost = 3;
    private String forestFilename = "content-forest.json";

    public CreateContentDatabasesCommand() {
        setExecuteSortOrder(SortOrderConstants.CREATE_CONTENT_DATABASES);
    }

    @Override
    public Integer getUndoSortOrder() {
        return SortOrderConstants.DELETE_CONTENT_DATABASES;
    }

    @Override
    public void execute(CommandContext context) {
        AppConfig appConfig = context.getAppConfig();

        JsonNode node = mergeContentDatabaseFiles(appConfig);
        if (node == null) {
            logger.info("No content database files found, so no creating or updating content databases");
            return;
        }

        String payload = node.toString();
        String json = tokenReplacer.replaceTokens(payload, appConfig, false);

        DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());
        SaveReceipt receipt = dbMgr.save(json);
        createForestsIfDatabaseWasJustCreated(receipt, context);

        if (appConfig.isTestPortSet()) {
            json = tokenReplacer.replaceTokens(payload, appConfig, true);
            receipt = dbMgr.save(json);
            createForestsIfDatabaseWasJustCreated(receipt, context);
        }
    }

    protected void createForestsIfDatabaseWasJustCreated(SaveReceipt receipt, CommandContext context) {
        // Location header is only set when the database has just been created
        if (receipt.hasLocationHeader()) {
            if (logger.isInfoEnabled()) {
                logger.info("Creating forests for newly created database: " + receipt.getResourceId());
            }
            CreateForestsCommand c = new CreateForestsCommand();
            c.setForestsPerHost(forestsPerHost);
            c.setForestFilename(forestFilename);
            c.setDatabaseName(receipt.getResourceId());
            c.setForestPayload(CreateForestsCommand.DEFAULT_FOREST_PAYLOAD);
            c.execute(context);
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
            dbMgr.setForestDelete(forestDelete);
            dbMgr.delete(json);

            if (appConfig.isTestPortSet()) {
                json = tokenReplacer.replaceTokens(payload, appConfig, true);
                dbMgr.delete(json);
            }
        } else {
            // Try to delete the content database if it exists
            DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());
            dbMgr.setForestDelete(forestDelete);
            dbMgr.deleteByName(appConfig.getContentDatabaseName());

            if (appConfig.isTestPortSet()) {
                dbMgr.deleteByName(appConfig.getContentDatabaseName());
            }
        }
    }

    protected JsonNode mergeContentDatabaseFiles(AppConfig appConfig) {
        List<File> files = appConfig.getConfigDir().getContentDatabaseFiles();
        if (logger.isInfoEnabled()) {
            logger.info("Merging JSON files at locations: " + files);
        }
        return JsonNodeUtil.mergeJsonFiles(files);
    }

    public String getForestDelete() {
        return forestDelete;
    }

    public void setForestDelete(String forestDelete) {
        this.forestDelete = forestDelete;
    }

    public int getForestsPerHost() {
        return forestsPerHost;
    }

    public void setForestsPerHost(int forestsPerHost) {
        this.forestsPerHost = forestsPerHost;
    }

    public String getForestFilename() {
        return forestFilename;
    }

    public void setForestFilename(String forestFilename) {
        this.forestFilename = forestFilename;
    }
}
