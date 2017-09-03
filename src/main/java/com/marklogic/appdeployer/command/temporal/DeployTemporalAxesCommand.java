package com.marklogic.appdeployer.command.temporal;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.temporal.TemporalAxesManager;

import java.io.File;

public class DeployTemporalAxesCommand extends AbstractResourceCommand {

	private TemporalAxesManager currentTemporalAxesManager;

	public DeployTemporalAxesCommand() {
		// TODO - verify that range element indexes exist before creation of temporal axes?
		setExecuteSortOrder(SortOrderConstants.DEPLOY_TEMPORAL_AXIS);
		//can't delete temporal axes until able to delete temporal collections...
		setDeleteResourcesOnUndo(false);
	}

	@Override
	public void execute(CommandContext context) {
		AppConfig appConfig = context.getAppConfig();
		deployTemporalAxes(context, appConfig.getConfigDir(), appConfig.getContentDatabaseName());

		for (File dir : appConfig.getConfigDir().getDatabaseResourceDirectories()) {
			deployTemporalAxes(context, new ConfigDir(dir), dir.getName());
		}
	}

	protected void deployTemporalAxes(CommandContext context, ConfigDir configDir, String databaseIdOrName) {
		currentTemporalAxesManager = new TemporalAxesManager(context.getManageClient(), databaseIdOrName);
		processExecuteOnResourceDir(context, configDir.getTemporalAxesDir());
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
		return currentTemporalAxesManager;
	}

}
