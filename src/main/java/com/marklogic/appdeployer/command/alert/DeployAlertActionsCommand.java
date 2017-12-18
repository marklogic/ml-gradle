package com.marklogic.appdeployer.command.alert;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.alert.AlertActionManager;

import java.io.File;

/**
 * This is similar to how DeployTargetsCommand works for Flexrep. Since action belong to a certain alert config, this
 * command looks for every directory under alert/configs that ends with "-action". For each such directory, the alert
 * config name is determined by stripping "-actions" off the directory name, and then each actions JSON/XML file in the
 * directory is loaded for that alert config name.
 * <p>
 * Then, for every action that's loaded, a check is made for a directory with the name
 * "(action file name minus extension)-rules". The action file name is used because an alert name can contain symbols
 * like ":", which are not allowed in directory names. If that directory exists, then every file in that directory is
 * loaded as an alert rule for the action.
 */
public class DeployAlertActionsCommand extends AbstractCommand {

	private String actionsDirectorySuffix = "-actions";

	public DeployAlertActionsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_ALERT_ACTIONS);
	}

	@Override
	public void execute(CommandContext context) {
		AppConfig appConfig = context.getAppConfig();
		for (ConfigDir configDir : appConfig.getConfigDirs()) {
			deployActions(context, configDir, appConfig.getContentDatabaseName());

			for (File dir : configDir.getDatabaseResourceDirectories()) {
				deployActions(context, new ConfigDir(dir), dir.getName());
			}
		}
	}

	protected void deployActions(CommandContext context, ConfigDir configDir, String databaseIdOrName) {
		File configsDir = configDir.getAlertConfigsDir();
		if (configsDir != null && configsDir.exists()) {
			for (File f : configsDir.listFiles()) {
				if (f.isDirectory() && f.getName().endsWith(actionsDirectorySuffix)) {
					deployActionsInDirectory(f, context, databaseIdOrName);
				}
			}
		}
	}

	protected void deployActionsInDirectory(File dir, CommandContext context, String databaseIdOrName) {
		String configUri = extractConfigUriFromDirectory(dir);

		if (logger.isInfoEnabled()) {
			logger.info(format("Deploying alert actions with config URI '%s' in directory: %s", configUri,
				dir.getAbsolutePath()));
		}

		AlertActionManager mgr = new AlertActionManager(context.getManageClient(), databaseIdOrName, configUri);
		for (File f : listFilesInDirectory(dir)) {
			saveResource(mgr, context, f);
		}
	}

	protected String extractConfigUriFromDirectory(File dir) {
		String name = dir.getName();
		return name.substring(0, name.length() - actionsDirectorySuffix.length());
	}

	public void setActionsDirectorySuffix(String targetDirectorySuffix) {
		this.actionsDirectorySuffix = targetDirectorySuffix;
	}
}
