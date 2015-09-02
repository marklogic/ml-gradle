package com.rjrudin.marklogic.appdeployer.command.databases;

import java.io.File;

import com.rjrudin.marklogic.appdeployer.AppConfig;
import com.rjrudin.marklogic.appdeployer.command.AbstractCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.appdeployer.command.UndoableCommand;
import com.rjrudin.marklogic.appdeployer.command.forests.DeployForestsCommand;
import com.rjrudin.marklogic.mgmt.SaveReceipt;
import com.rjrudin.marklogic.mgmt.databases.DatabaseManager;

/**
 * Can be used for creating any kind of database with any sorts of forests. Specifying a config file for the database or
 * for the forests is optional. In order to create forests with different parameters, use DeployForestsCommand.
 */
public class DeployDatabaseCommand extends AbstractCommand implements UndoableCommand {

    /**
     * Optional XML/JSON filename for the database
     */
    private String databaseFilename;

    /**
     * Provide an easy way of creating a database based on a name without a file being provided. If this is false and
     * databaseFilename is null, then no database will be deployed.
     */
    private boolean createDatabaseWithoutFile = false;

    /**
     * The name of the database to be deployed; used for constructing forest names, and thus required if you're creating
     * forests.
     */
    private String databaseName;

    /**
     * Optional name of the file in the forests directory that will be used to create each forest. If not provided, a
     * "vanilla" forest is created on each host with a name based on the databaseName attribute.
     */
    private String forestFilename;

    /**
     * Number of forests to create per host for this database.
     */
    private int forestsPerHost = 1;

    /**
     * Applied when the database is deleted - see
     * http://docs.marklogic.com/REST/DELETE/manage/v2/databases/[id-or-name].
     */
    private String forestDelete = "data";

    private int undoSortOrder;

    public DeployDatabaseCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_OTHER_DATABASES);
        setUndoSortOrder(SortOrderConstants.DELETE_OTHER_DATABASES);
    }

    @Override
    public Integer getUndoSortOrder() {
        return undoSortOrder;
    }

    @Override
    public void execute(CommandContext context) {
        AppConfig appConfig = context.getAppConfig();
        String payload = getPayload(context);
        if (payload != null) {
            String json = tokenReplacer.replaceTokens(payload, appConfig, false);
            DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());
            SaveReceipt receipt = dbMgr.save(json);
            createForestsIfDatabaseWasJustCreated(receipt, context);
        }
    }

    @Override
    public void undo(CommandContext context) {
        AppConfig appConfig = context.getAppConfig();
        String payload = getPayload(context);
        if (payload != null) {
            String json = tokenReplacer.replaceTokens(payload, appConfig, false);
            DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());
            dbMgr.setForestDelete(forestDelete);
            dbMgr.delete(json);
        }
    }

    protected String getPayload(CommandContext context) {
        File f = null;
        if (databaseFilename != null) {
            f = new File(context.getAppConfig().getConfigDir().getDatabasesDir(), databaseFilename);
        }
        if (f != null && f.exists()) {
            return copyFileToString(f);
        } else if (createDatabaseWithoutFile) {
            return buildDefaultDatabasePayload(context);
        } else {
            logger.warn(format("Database file '%s' does not exist, so not executing", databaseFilename));
            return null;
        }
    }

    protected void createForestsIfDatabaseWasJustCreated(SaveReceipt receipt, CommandContext context) {
        // Location header is only set when the database has just been created
        if (receipt.hasLocationHeader()) {
            if (logger.isInfoEnabled()) {
                logger.info("Creating forests for newly created database: " + receipt.getResourceId());
            }
            DeployForestsCommand c = new DeployForestsCommand();
            c.setForestsPerHost(forestsPerHost);
            c.setForestFilename(forestFilename);
            c.setDatabaseName(receipt.getResourceId());
            c.setForestPayload(DeployForestsCommand.DEFAULT_FOREST_PAYLOAD);
            c.execute(context);
        }
    }

    protected String buildDefaultDatabasePayload(CommandContext context) {
        return format("{\"database-name\": \"%s\"}", databaseName);
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

    public void setUndoSortOrder(int undoSortOrder) {
        this.undoSortOrder = undoSortOrder;
    }

    public String getDatabaseFilename() {
        return databaseFilename;
    }

    public void setDatabaseFilename(String databaseFilename) {
        this.databaseFilename = databaseFilename;
    }

    public boolean isCreateDatabaseWithoutFile() {
        return createDatabaseWithoutFile;
    }

    public void setCreateDatabaseWithoutFile(boolean createDatabaseWithoutFile) {
        this.createDatabaseWithoutFile = createDatabaseWithoutFile;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
}
