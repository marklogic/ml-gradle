package com.marklogic.appdeployer.command.temporal;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.temporal.TemporalAxesManager;
import com.marklogic.mgmt.resource.temporal.TemporalCollectionManager;

import java.io.File;

public class DeployTemporalCollectionsCommand extends AbstractResourceCommand {

	private TemporalCollectionManager currentTemporalCollectionManager;

	public DeployTemporalCollectionsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_TEMPORAL_COLLECTIONS);
		// if the temporal collection contains documents, then the delete operation will fail
		// TODO - could add check to get count of documents in temporal collection. If zero docs, then can delete
		setDeleteResourcesOnUndo(false);
	}

	@Override
	public void execute(CommandContext context) {
		AppConfig appConfig = context.getAppConfig();
		for (ConfigDir configDir : appConfig.getConfigDirs()) {
			deployTemporalCollections(context, configDir, appConfig.getContentDatabaseName());
			for (File dir : configDir.getDatabaseResourceDirectories()) {
				deployTemporalCollections(context, new ConfigDir(dir), dir.getName());
			}
		}
	}

	protected void deployTemporalCollections(CommandContext context, ConfigDir configDir, String databaseIdOrName) {
		currentTemporalCollectionManager = new TemporalCollectionManager(context.getManageClient(), databaseIdOrName);
		processExecuteOnResourceDir(context, configDir.getTemporalCollectionsDir());
	}

	/**
	 * Not used since we override execute in the parent class.
	 *
	 * @param context
	 * @return
	 */
	@Override
	protected File[] getResourceDirs(CommandContext context) {
		return null;
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return currentTemporalCollectionManager;
	}
}
