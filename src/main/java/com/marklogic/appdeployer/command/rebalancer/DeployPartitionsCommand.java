package com.marklogic.appdeployer.command.rebalancer;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.rebalancer.PartitionManager;

import java.io.File;

public class DeployPartitionsCommand extends AbstractResourceCommand {

	private PartitionManager currentPartitionManager;

	public DeployPartitionsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_PARTITIONS);

		// Partitions are expected to be deleted when the parent database is deleted
		setDeleteResourcesOnUndo(false);
	}

	@Override
	public void execute(CommandContext context) {
		AppConfig appConfig = context.getAppConfig();
		for (ConfigDir configDir : appConfig.getConfigDirs()) {
			for (File dir : configDir.getDatabaseResourceDirectories()) {
				String databaseName = determineDatabaseNameForDatabaseResourceDirectory(context, configDir, dir);
				if (databaseName != null) {
					deployPartitions(context, new ConfigDir(dir), databaseName);
				}
			}
		}
	}

	protected void deployPartitions(CommandContext context, ConfigDir configDir, String databaseIdOrName) {
		currentPartitionManager = new PartitionManager(context.getManageClient(), databaseIdOrName);
		processExecuteOnResourceDir(context, configDir.getPartitionsDir());
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return currentPartitionManager;
	}

	@Override
	protected File[] getResourceDirs(CommandContext context) {
		return findResourceDirs(context, configDir -> configDir.getPartitionsDir());
	}
}
