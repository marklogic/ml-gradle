package com.marklogic.appdeployer.command.alert;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.alert.AlertConfigManager;

import java.io.File;

public class DeployAlertConfigsCommand extends AbstractResourceCommand {

	private AlertConfigManager currentAlertConfigManager;

	public DeployAlertConfigsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_ALERT_CONFIGS);
		setDeleteResourcesOnUndo(false);
	}

	@Override
	public void execute(CommandContext context) {
		AppConfig appConfig = context.getAppConfig();
		for (ConfigDir configDir : appConfig.getConfigDirs()) {
			deployAlertConfigs(context, configDir, appConfig.getContentDatabaseName());
			for (File dir : configDir.getDatabaseResourceDirectories()) {
				String databaseName = determineDatabaseNameForDatabaseResourceDirectory(context, configDir, dir);
				deployAlertConfigs(context, new ConfigDir(dir), databaseName);
			}
		}
	}

	protected void deployAlertConfigs(CommandContext context, ConfigDir configDir, String databaseIdOrName) {
		currentAlertConfigManager = new AlertConfigManager(context.getManageClient(), databaseIdOrName);
		processExecuteOnResourceDir(context, configDir.getAlertConfigsDir());
	}

	/**
	 * Not used, as we override execute in this command.
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
		return currentAlertConfigManager;
	}
}
