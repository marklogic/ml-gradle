package com.marklogic.appdeployer.command.flexrep;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.flexrep.ConfigManager;

import java.io.File;

import java.io.File;

/**
 * Defaults to the content database name in the AppConfig instance. Can be overridden via the databaseNameOrId property.
 */
public class DeployConfigsCommand extends AbstractResourceCommand {

	private ConfigManager currentConfigManager;

	public DeployConfigsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_FLEXREP_CONFIGS);
		// Flexrep config is stored in a database, so we don't need to delete it as the database will be deleted
		setDeleteResourcesOnUndo(false);
	}

	@Override
	public void execute(CommandContext context) {
		AppConfig appConfig = context.getAppConfig();
		for (ConfigDir configDir : appConfig.getConfigDirs()) {
			deployConfigs(context, configDir, appConfig.getContentDatabaseName());
			for (File dir : configDir.getDatabaseResourceDirectories()) {
				deployConfigs(context, new ConfigDir(dir), dir.getName());
			}
		}
	}

	protected void deployConfigs(CommandContext context, ConfigDir configDir, String databaseIdOrName) {
		currentConfigManager = new ConfigManager(context.getManageClient(), databaseIdOrName);
		processExecuteOnResourceDir(context, configDir.getFlexrepConfigsDir());
	}

	/**
	 * Not used as we override execute in this command.
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
		return currentConfigManager;
	}

}
