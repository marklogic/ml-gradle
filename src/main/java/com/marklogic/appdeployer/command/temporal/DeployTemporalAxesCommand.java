package com.marklogic.appdeployer.command.temporal;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.temporal.TemporalAxesManager;

import java.io.File;

public class DeployTemporalAxesCommand extends AbstractResourceCommand {

	private String databaseIdOrName;

	public DeployTemporalAxesCommand() {
		// TODO - verify that range element indexes exist before creation of temporal axes?
		setExecuteSortOrder(SortOrderConstants.DEPLOY_TEMPORAL_AXIS);
		//can't delete temporal axes until able to delete temporal collections...
		setDeleteResourcesOnUndo(false);
	}

	@Override
	protected File[] getResourceDirs(CommandContext context) {
		return new File[] { new File(context.getAppConfig().getConfigDir().getTemporalDir(),"axes") };
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		String db = databaseIdOrName != null ? databaseIdOrName : context.getAppConfig().getContentDatabaseName();
		return new TemporalAxesManager(context.getManageClient(), db);
	}

	public void setDatabaseIdOrName(String databaseIdOrName) {
		this.databaseIdOrName = databaseIdOrName;
	}

}
