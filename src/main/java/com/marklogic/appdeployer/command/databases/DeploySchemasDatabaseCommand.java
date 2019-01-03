package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.command.SortOrderConstants;

/**
 * This command looks for a specific database file for deploying a schemas database. This schemas database is considered
 * to be part of the "main" REST API server and content database.
 *
 * Deprecated in 3.11.0 - ever since sorting was added to DeployOtherDatabasesCommand, there's been no need for
 * this command.
 */
@Deprecated
public class DeploySchemasDatabaseCommand extends DeployDatabaseCommand {

	public final static String DATABASE_FILENAME = "schemas-database.json";

	public DeploySchemasDatabaseCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_SCHEMAS_DATABASE);
		setUndoSortOrder(SortOrderConstants.DELETE_SCHEMAS_DATABASE);
		setDatabaseFilename(DATABASE_FILENAME);
		setCreateForestsOnEachHost(false);
	}
}
