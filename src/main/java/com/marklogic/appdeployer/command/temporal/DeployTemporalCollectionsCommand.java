package com.marklogic.appdeployer.command.temporal;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.ResourceManager;
import com.marklogic.mgmt.temporal.TemporalCollectionManager;

import java.io.File;

public class DeployTemporalCollectionsCommand extends AbstractResourceCommand {

	private String databaseIdOrName;

	public DeployTemporalCollectionsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_TEMPORAL_COLLECTIONS);
		// if the temporal collection contains documents, then the delete operation will fail
		// TODO - could add check to get count of documents in temporal collection. If zero docs, then can delete
		setDeleteResourcesOnUndo(false);
	}

	@Override
	protected File[] getResourceDirs(CommandContext context) {
		return new File[] { new File(context.getAppConfig().getConfigDir().getTemporalDir(),"collections") };
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		String db = databaseIdOrName != null ? databaseIdOrName : context.getAppConfig().getContentDatabaseName();
		return new TemporalCollectionManager(context.getManageClient(), db);
	}

	public void setDatabaseIdOrName(String databaseIdOrName) {
		this.databaseIdOrName = databaseIdOrName;
	}
}
