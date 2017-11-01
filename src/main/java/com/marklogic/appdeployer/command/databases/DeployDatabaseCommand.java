package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.appdeployer.command.UndoableCommand;
import com.marklogic.appdeployer.command.forests.DeployForestsCommand;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.mgmt.resource.databases.DatabaseManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
     * If true, this will look for a ./forests/(name of database) directory that defines custom forests for this
     * database. If such a directory exists, this command will not create any forests for the database. If the
     * directory does not exist, then this command will create forests for the database as defined by forestsPerHost.
     */
    private boolean checkForCustomForests = true;

    /**
     * Optional name of the file in the forests directory that will be used to create each forest. If not provided, a
     * "vanilla" forest is created on each host with a name based on the databaseName attribute. This is ignored if
     * custom forests are found in ./forests/(name of forest).
     */
    private String forestFilename;

    /**
     * Number of forests to create per host for this database. This is ignored if custom forests are found in
     * ./forests/(name of forest).
     */
    private int forestsPerHost = 1;

    /**
     * Passed on to DeployForestsCommand.
     */
    private boolean createForestsOnEachHost = true;

    private int undoSortOrder;

    private boolean subDatabase = false;
    private String superDatabaseName;


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

            databaseName = receipt.getResourceId();
            if (shouldCreateForests(context, payload)) {
	            buildDeployForestsCommand(payload, receipt, context).execute(context);
            } else {
            	if (logger.isInfoEnabled()) {
            		logger.info("Found custom forests for database, so not creating default forests");
	            }
            }
            // subdatabases? create and attach
            if(!isSubDatabase()){
            	this.addSubDatabases(dbMgr, context, receipt.getResourceId());
            }

        }
    }

    @Override
    public void undo(CommandContext context) {
        String payload = buildPayload(context);
        if (payload != null) {
        	DatabaseManager dbMgr = newDatabaseManageForDeleting(context);
        	// if this has subdatabases, detach/delete them first
        	if(!isSubDatabase()){
        		removeSubDatabases(dbMgr, context, dbMgr.getResourceId(payload));
        	}
            dbMgr.delete(payload);
        }
    }

    /**
     * Creates and attaches sub-databases to a the specified database, making it a super-database.
     * Note: Sub-databases are expected to have a configuration files in databases/subdatabases/<super-database-name>
     * @param dbMgr
     * @param context
     * @param superDatabaseName Name of the database the sub-databases are to be associated with
     */
    protected void addSubDatabases(DatabaseManager dbMgr, CommandContext context, String superDatabaseName) {
        File subdbDir = new File(context.getAppConfig().getConfigDir().getDatabasesDir() + File.separator + "subdatabases" + File.separator + superDatabaseName);
        logger.info(format("Checking for sub-databases in: %s for database: %s", subdbDir.getAbsolutePath(), superDatabaseName));
        if(subdbDir.exists()){
        	List<String> subDbNames = new ArrayList<String>();
            for (File f : listFilesInDirectory(subdbDir)) {
                logger.info(format("Will process sub database for %s found in file: %s", superDatabaseName, f.getAbsolutePath()));
                DeployDatabaseCommand subDbCommand = new DeployDatabaseCommand();
                subDbCommand.setDatabaseFilename(f.getName());
                subDbCommand.setSuperDatabaseName(superDatabaseName);
                subDbCommand.setSubDatabase(true);
                subDbCommand.execute(context);
                subDbNames.add(subDbCommand.getDatabaseName());
                logger.info(format("Created subdatabase %s for database %s", subDbCommand.getDatabaseName(), superDatabaseName));
            }
            if(subDbNames.size() > 0){
            	dbMgr.attachSubDatabases(superDatabaseName, subDbNames);
            }
        }
    }

    /**
     * Detaches and deletes all sub-databases for the specified super-database
     * @param dbMgr
     * @param context
     * @param superDatabaseName
     */
    protected void removeSubDatabases(DatabaseManager dbMgr, CommandContext context, String superDatabaseName){
		File subdbDir = new File(context.getAppConfig().getConfigDir().getDatabasesDir() + File.separator + "subdatabases" + File.separator + superDatabaseName);
        logger.info(format("Checking to see if %s has subdatabases that need to be removed. Looking in folder: %s", superDatabaseName, subdbDir.getAbsolutePath()));
        if(subdbDir.exists()){
        	logger.info("Removing all subdatabases from database: " + superDatabaseName);
        	dbMgr.detachSubDatabases(superDatabaseName);
            for (File f : listFilesInDirectory(subdbDir)) {
                DeployDatabaseCommand subDbCommand = new DeployDatabaseCommand();
                subDbCommand.setDatabaseFilename(f.getName());
                subDbCommand.setSuperDatabaseName(superDatabaseName);
                subDbCommand.setSubDatabase(true);
                subDbCommand.undo(context);

            }
        }
    }


    /**
     * Configures the DatabaseManager in terms of how it deletes forests based on properties in the AppConfig instance
     * in the CommandContext.
     *
     * @param context
     * @return
     */
    protected DatabaseManager newDatabaseManageForDeleting(CommandContext context) {
        DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());
        dbMgr.setForestDelete(getForestDeleteLevel(context.getAppConfig()));
        dbMgr.setDeleteReplicas(context.getAppConfig().isDeleteReplicas());
        return dbMgr;
    }

    protected String getForestDeleteLevel(AppConfig appConfig) {
        return appConfig.isDeleteForests() ? DatabaseManager.DELETE_FOREST_DATA : DatabaseManager.DELETE_FOREST_CONFIGURATION;
    }

    /**
     * Builds the XML or JSON payload for this command, based on the given CommandContext.
     *
     * @param context
     * @return
     */
    public String buildPayload(CommandContext context) {
        String payload = getPayload(context);
        return payload != null ? payloadTokenReplacer.replaceTokens(payload, context.getAppConfig(), false) : null;
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
        	if(isSubDatabase()){
        		String subDbFileName =context.getAppConfig().getConfigDir().getDatabasesDir() + File.separator + "subdatabases" + File.separator + this.getSuperDatabaseName() + File.separator + databaseFilename;
                f = new File(subDbFileName);
        	}else {
        		f = new File(context.getAppConfig().getConfigDir().getDatabasesDir(), databaseFilename);
        	}
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
	 * This is where we check to see if a custom forests directory exists at ./forests/(database name). The database
	 * name is extracted from the payload via a PayloadParser. This check can be disabled by setting
	 * checkForCustomForests to false.
	 *
	 * @param context
	 * @param payload
	 * @return
	 */
	protected boolean shouldCreateForests(CommandContext context, String payload) {
		if (isCheckForCustomForests()) {
			PayloadParser parser = new PayloadParser();
			String dbName = parser.getPayloadFieldValue(payload, "database-name");
			return !customForestsExist(context, dbName);
		}
		return true;
	}

	/**
	 * @param context
	 * @param dbName
	 * @return true if any file exists in ./forests/(name of database)
	 */
	protected boolean customForestsExist(CommandContext context, String dbName) {
    	File dir = context.getAppConfig().getConfigDir().getForestsDir();
    	if (dir.exists()) {
    		File dbDir = new File(dir, dbName);
    		if (dbDir.exists()) {
    			return dbDir.listFiles().length > 0;
		    }
	    }
	    return false;
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

    public boolean isCheckForCustomForests() {
        return checkForCustomForests;
    }

    public void setCheckForCustomForests(boolean checkForCustomForests) {
        this.checkForCustomForests = checkForCustomForests;
    }

	public void setSubDatabase(boolean isSubDatabase){
		this.subDatabase = isSubDatabase;
	}

	public boolean isSubDatabase() {
		return this.subDatabase;
	}

	public void setSuperDatabaseName(String name){
		this.superDatabaseName = name;
	}

	public String getSuperDatabaseName() {
		return this.superDatabaseName;
	}
}
