/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.rebalancer;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.rebalancer.PartitionQueryManager;

import java.io.File;

public class DeployPartitionQueriesCommand extends AbstractResourceCommand {

	private PartitionQueryManager currentPartitionQueryManager;

	public DeployPartitionQueriesCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_PARTITION_QUERIES);

		// Partition queries are expected to be deleted when the parent database is deleted
		setDeleteResourcesOnUndo(false);
	}

	@Override
	public void execute(CommandContext context) {
		AppConfig appConfig = context.getAppConfig();
		for (ConfigDir configDir : appConfig.getConfigDirs()) {
			for (File dir : configDir.getDatabaseResourceDirectories()) {
				String databaseName = determineDatabaseNameForDatabaseResourceDirectory(context, configDir, dir);
				if (databaseName != null) {
					deployPartitionQueries(context, new ConfigDir(dir), databaseName);
				}
			}
		}
	}

	protected void deployPartitionQueries(CommandContext context, ConfigDir configDir, String databaseIdOrName) {
		currentPartitionQueryManager = new PartitionQueryManager(context.getManageClient(), databaseIdOrName);
		processExecuteOnResourceDir(context, configDir.getPartitionQueriesDir());
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return currentPartitionQueryManager;
	}

	@Override
	protected File[] getResourceDirs(CommandContext context) {
		return findResourceDirs(context, configDir -> configDir.getPartitionQueriesDir());
	}
}
