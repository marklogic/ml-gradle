package com.marklogic.appdeployer.command.databases;

import java.io.File;
import java.util.Map;

import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.appdeployer.command.UndoableCommand;
import com.marklogic.appdeployer.command.forests.DeployForestsCommand;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.mgmt.databases.DatabaseManager;

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
     * The name of the database to be deployed; only needs to be set if the database payload is automatically generated
     * instead of being loaded from a file.
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
     * Passed on to DeployForestsCommand.
     */
    private boolean createForestsOnEachHost = true;

    /**
     * Applied when the database is deleted - see
     * http://docs.marklogic.com/REST/DELETE/manage/v2/databases/[id-or-name].
     */
    private String forestDelete = "data";

    private boolean deleteReplicas = true;

    private int undoSortOrder;

    public DeployDatabaseCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_OTHER_DATABASES);
        setUndoSortOrder(SortOrderConstants.DELETE_OTHER_DATABASES);
    }

    public DeployDatabaseCommand(String databaseFilename) {
        this();
        this.databaseFilename = databaseFilename;
    }

    @Override
    public String toString() {
        return databaseFilename;
    }

    @Override
    public Integer getUndoSortOrder() {
        return undoSortOrder;
    }

    @Override
    public void execute(CommandContext context) {
        String payload = buildPayload(context);
        if (payload != null) {
            DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());
            SaveReceipt receipt = dbMgr.save(payload);
            buildDeployForestsCommand(payload, receipt, context).execute(context);
        }
    }

    @Override
    public void undo(CommandContext context) {
        String payload = buildPayload(context);
        if (payload != null) {
            DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());
            dbMgr.setForestDelete(forestDelete);
            dbMgr.setDeleteReplicas(deleteReplicas);
            dbMgr.delete(payload);
        }
    }

    /**
     * Builds the XML or JSON payload for this command, based on the given CommandContext.
     * 
     * @param context
     * @return
     */
    public String buildPayload(CommandContext context) {
        String payload = getPayload(context);
        return payload != null ? tokenReplacer.replaceTokens(payload, context.getAppConfig(), false) : null;
    }

    /**
     * Get the payload based on the given CommandContext. Only loads the payload, does not replace any tokens in it.
     * Call buildPayload to construct a payload with all tokens replaced.
     * 
     * @param context
     * @return
     */
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
            if (logger.isInfoEnabled()) {
                logger.info(format("Database file '%s' does not exist, so not executing", databaseFilename));
            }
            return null;
        }
    }

    /**
     * Allows for how an instance of DeployForestsCommand is built to be overridden by a subclass.
     * 
     * @param dbPayload
     *            Needed so we can look up forest counts based on the database name
     * @param receipt
     * @param context
     * @return
     */
    protected DeployForestsCommand buildDeployForestsCommand(String dbPayload, SaveReceipt receipt,
            CommandContext context) {
        DeployForestsCommand c = new DeployForestsCommand();
        c.setCreateForestsOnEachHost(createForestsOnEachHost);
        c.setForestsPerHost(determineForestCountPerHost(dbPayload, context));
        c.setForestFilename(forestFilename);
        c.setDatabaseName(receipt.getResourceId());
        c.setForestPayload(DeployForestsCommand.DEFAULT_FOREST_PAYLOAD);
        return c;
    }

    /**
     * Checks the forestCounts map in AppConfig to see if the client has specified a number of forests per host for this
     * database.
     * 
     * @param dbPayload
     * @param context
     * @return
     */
    protected int determineForestCountPerHost(String dbPayload, CommandContext context) {
        int forestCount = forestsPerHost;
        if (dbPayload != null) {
            try {
                String dbName = new PayloadParser().getPayloadFieldValue(dbPayload, "database-name");
                Map<String, Integer> forestCounts = context.getAppConfig().getForestCounts();
                if (forestCounts != null && forestCounts.containsKey(dbName)) {
                    Integer i = forestCounts.get(dbName);
                    if (i != null) {
                        forestCount = i;
                    }
                }
            } catch (Exception ex) {
                logger.warn("Unable to determine forest counts, cause: " + ex.getMessage(), ex);
            }
        }
        return forestCount;
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

    public boolean isCreateForestsOnEachHost() {
        return createForestsOnEachHost;
    }

    public void setCreateForestsOnEachHost(boolean createForestsOnEachHost) {
        this.createForestsOnEachHost = createForestsOnEachHost;
    }

    public boolean isDeleteReplicas() {
        return deleteReplicas;
    }

    public void setDeleteReplicas(boolean deleteReplicas) {
        this.deleteReplicas = deleteReplicas;
    }
}
