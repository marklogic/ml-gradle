/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.appdeployer.command.UndoableCommand;
import com.marklogic.appdeployer.command.forests.DeployForestsCommand;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.api.database.Database;
import com.marklogic.mgmt.resource.databases.DatabaseManager;

import java.io.File;
import java.util.Set;

/**
 * Can be used for creating any kind of database with any sorts of forests. Specifying a config file for the database or
 * for the forests is optional. In order to create forests with different parameters, use DeployForestsCommand.
 */
public class DeployDatabaseCommand extends AbstractCommand implements UndoableCommand {

	/**
	 * Optional XML/JSON file for the database. If set, databaseFilename is ignored.
	 */
	private File databaseFile;

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
     * Passed on to DeployForestsCommand. If forests are to be created, controls whether forests on created on every
     * host or only one one host.
     */
    @Deprecated
    private boolean createForestsOnEachHost = true;

    private int undoSortOrder;

    private boolean subDatabase = false;
    private String superDatabaseName;

	/**
	 * To optimize the creation of forests for many databases, this command can have its forest creation postponed. The
	 * command will still construct a DeployForestsCommand, which a client is then expected to retrieve later, as that
	 * command defines all of the forests to be created.
	 */
	private boolean postponeForestCreation = false;
    private DeployForestsCommand deployForestsCommand;

    // This is expected to be set via DeployOtherDatabasesCommand
    private String payload;

	/**
	 * Expected to be set by DeployOtherDatabasesCommand; a list of database names (should default to the ones MarkLogic
	 * provides out-of-the-box) that won't be undeployed during an "undo" operation.
	 */
	private Set<String> databasesToNotUndeploy;

	private DeployDatabaseCommandFactory deployDatabaseCommandFactory = new DefaultDeployDatabaseCommandFactory();

    public DeployDatabaseCommand() {
	    setExecuteSortOrder(SortOrderConstants.DEPLOY_OTHER_DATABASES);
        setUndoSortOrder(SortOrderConstants.DELETE_OTHER_DATABASES);
        setResourceClassType(Database.class);
    }

    public DeployDatabaseCommand(File databaseFile) {
    	this();
    	this.databaseFile = databaseFile;
    }

    public DeployDatabaseCommand(String databaseFilename) {
        this();
        this.databaseFilename = databaseFilename;
    }

    @Override
    public String toString() {
        if (databaseFile != null) {
        	return databaseFile.getAbsolutePath();
        }
    	return databaseFilename;
    }

    @Override
    public Integer getUndoSortOrder() {
        return undoSortOrder;
    }

	@Override
    public void execute(CommandContext context) {
        String payload = buildPayloadForSaving(context);
        if (payload != null) {
	        DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());
	        databaseName = dbMgr.getResourceId(payload);

            dbMgr.save(payload);

            DeployForestsCommand tempCommand = buildDeployForestsCommand(databaseName, context);
            if (tempCommand != null) {
            	this.deployForestsCommand = tempCommand;
	            if (postponeForestCreation) {
		            logger.info("Postponing creation of forests for database: " + databaseName);
	            } else {
		            deployForestsCommand.execute(context);
	            }
            }

            deploySubDatabases(this.databaseName, context);
        }
    }

	/**
	 * If this is not a sub-database, then deploy sub-databases if any have been configured for this database.
	 *
	 * @param context
	 */
	public void deploySubDatabases(String dbName, CommandContext context) {
	    if (!isSubDatabase()) {
		    new DeploySubDatabasesCommand(dbName, deployDatabaseCommandFactory).execute(context);
	    }
    }

	/**
	 * Performs all work necessary to construct a payload that can either be saved immediately or included in a CMA
	 * configuration.
	 *
	 * @param context
	 * @return
	 */
	public String buildPayloadForSaving(CommandContext context) {
	    String payload = buildPayload(context);
	    if (payload != null) {
		    payload = adjustPayloadBeforeSavingResource(context, null, payload);
	    }
	    return payload;
    }

	/**
	 * When deleting databases, resource files are not yet merged together first. This should only mean that some
	 * unnecessary delete calls are made.
	 *
	 * @param context
	 */
	@Override
    public void undo(CommandContext context) {
        String payload = buildPayload(context);

        if (databasesToNotUndeploy != null) {
        	final String dbName = new PayloadParser().getPayloadFieldValue(payload, "database-name", false);
        	if (dbName != null && databasesToNotUndeploy.contains(dbName)) {
        		logger.info(format("Not undeploying database %s because it is in the list of database names to not undeploy.", dbName));
        		return;
	        }
        }

        if (payload != null) {
        	DatabaseManager dbMgr = newDatabaseManageForDeleting(context);

	        // if this has sub-databases, detach/delete them first
	        if (!isSubDatabase()) {
	        	final String dbName = dbMgr.getResourceId(payload);
	        	new DeploySubDatabasesCommand(dbName, deployDatabaseCommandFactory).undo(context);
	        }

            dbMgr.delete(payload);
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
    	if (this.payload != null) {
    		return payload;
	    }

        File f = null;
        if (this.databaseFile != null) {
        	f = this.databaseFile;
        }
        else if (databaseFilename != null) {
	        /**
	         * This is a little trickier in 3.3.0 - we only have a filename, so need to check every ConfigDir for the
	         * file. Last one wins.
	         */
        	if (isSubDatabase()) {
        		for (ConfigDir configDir : context.getAppConfig().getConfigDirs()) {
			        String subDbFileName = configDir.getDatabasesDir() + File.separator + "subdatabases" + File.separator + this.getSuperDatabaseName() + File.separator + databaseFilename;
			        File tmpFile = new File(subDbFileName);
			        if (tmpFile != null && tmpFile.exists()) {
			        	f = tmpFile;
			        }
		        }
        	} else {
        		for (ConfigDir configDir : context.getAppConfig().getConfigDirs()) {
        			File dbDir = configDir.getDatabasesDir();
        			if (dbDir != null && dbDir.exists()) {
        				File tmpFile = new File(dbDir, databaseFilename);
        				if (tmpFile != null && tmpFile.exists()) {
        					f = tmpFile;
				        }
			        }
		        }
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
	 * Determines if forests should be created after a database is deployed.
	 *
	 * This includes checking to see if a custom forests directory exists at ./forests/(database name). The database
	 * name is extracted from the payload via a PayloadParser. This check can be disabled by setting
	 * checkForCustomForests to false.
	 *
	 * @param databaseName
	 * @param context
	 * @return
	 */
	protected boolean shouldCreateForests(String databaseName, CommandContext context) {
		if (!context.getAppConfig().isCreateForests()) {
			if (logger.isInfoEnabled()) {
				logger.info("Forest creation is disabled, so not creating any forests");
			}
			return false;
		}

		if (isCheckForCustomForests()) {
			boolean customForestsDontExist = !customForestsExist(context, databaseName);
			if (!customForestsDontExist && logger.isInfoEnabled()) {
				logger.info("Found custom forests for database " + databaseName + ", so not creating default forests");
			}
			return customForestsDontExist;
		}
		return true;
	}

	/**
	 * @param context
	 * @param dbName
	 * @return true if any file exists in ./forests/(name of database)
	 */
	protected boolean customForestsExist(CommandContext context, String dbName) {
		for (ConfigDir configDir : context.getAppConfig().getConfigDirs()) {
			File dir = configDir.getForestsDir();
			if (dir.exists()) {
				File dbDir = new File(dir, dbName);
				if (dbDir.exists()) {
					return dbDir.listFiles().length > 0;
				}
			}
		}
	    return false;
    }

    /**
     * Initializes an instance of DeployForestsCommand. Public so that it can be accessed by a client that wishes to call
     * buildForests on the command.
     *
     * @param databaseName
     * @param context
     * @return will return null if it's determined that no forests should be created for the database
     */
    public DeployForestsCommand buildDeployForestsCommand(String databaseName, CommandContext context) {
    	if (shouldCreateForests(databaseName, context)) {
		    DeployForestsCommand c = new DeployForestsCommand(databaseName);
		    c.setForestsPerHost(getForestsPerHost());
		    c.setCreateForestsOnEachHost(createForestsOnEachHost);
		    c.setForestFilename(forestFilename);
		    return c;
	    }
    	return null;
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

    @Deprecated
    public boolean isCreateForestsOnEachHost() {
        return createForestsOnEachHost;
    }

	/**
	 * Use appConfig.setDatabasesWithForestsOnOneHost
	 * @param createForestsOnEachHost
	 */
	@Deprecated
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

	public void setDatabaseFile(File databaseFile) {
		this.databaseFile = databaseFile;
	}

	public Set<String> getDatabasesToNotUndeploy() {
		return databasesToNotUndeploy;
	}

	public void setDatabasesToNotUndeploy(Set<String> databasesToNotUndeploy) {
		this.databasesToNotUndeploy = databasesToNotUndeploy;
	}

	public void setDeployDatabaseCommandFactory(DeployDatabaseCommandFactory deployDatabaseCommandFactory) {
		this.deployDatabaseCommandFactory = deployDatabaseCommandFactory;
	}

	public void setPostponeForestCreation(boolean postponeForestCreation) {
		this.postponeForestCreation = postponeForestCreation;
	}

	public boolean isPostponeForestCreation() {
		return postponeForestCreation;
	}

	public DeployForestsCommand getDeployForestsCommand() {
		return deployForestsCommand;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}
}
