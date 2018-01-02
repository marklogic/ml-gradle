package com.marklogic.appdeployer.command.flexrep;

import java.io.File;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.flexrep.TargetManager;

import java.io.File;

/**
 * The directory structure for this is a bit different from most command. Since targets belong to a certain flexrep
 * config, this command looks for every directory under flexrep/configs that ends with "-targets". For each such
 * directory, the flexrep config name is determined by stripping "-targets" off the directory name, and then each target
 * JSON/XML file in the directory is loaded for that flexrep config name.
 */
public class DeployTargetsCommand extends AbstractCommand {

	private String targetDirectorySuffix = "-targets";

	public DeployTargetsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_FLEXREP_TARGETS);
	}

	@Override
	public void execute(CommandContext context) {
		AppConfig appConfig = context.getAppConfig();
		for (ConfigDir configDir : appConfig.getConfigDirs()) {
			deployTargets(context, configDir, appConfig.getContentDatabaseName());
			for (File dir : configDir.getDatabaseResourceDirectories()) {
				deployTargets(context, new ConfigDir(dir), dir.getName());
			}
		}
	}

	protected void deployTargets(CommandContext context, ConfigDir configDir, String databaseIdOrName) {
		File configsDir = configDir.getFlexrepConfigsDir();
		if (configsDir != null && configsDir.exists()) {
			for (File f : configsDir.listFiles()) {
				if (f.isDirectory() && f.getName().endsWith(targetDirectorySuffix)) {
					deployTargetsInDirectory(f, context, databaseIdOrName);
				}
			}
		} else {
			logResourceDirectoryNotFound(configsDir);
		}
	}

	protected void deployTargetsInDirectory(File dir, CommandContext context, String databaseIdOrName) {
		String configName = extractConfigNameFromDirectory(dir);

		if (logger.isInfoEnabled()) {
			logger.info(format("Deploying flexrep targets with config name '%s' in directory: %s", configName,
				dir.getAbsolutePath()));
		}

		TargetManager mgr = new TargetManager(context.getManageClient(), databaseIdOrName, configName);
		for (File f : listFilesInDirectory(dir)) {
			saveResource(mgr, context, f);
		}
	}

	protected String extractConfigNameFromDirectory(File dir) {
		String name = dir.getName();
		return name.substring(0, name.length() - targetDirectorySuffix.length());
	}

	public void setTargetDirectorySuffix(String targetDirectorySuffix) {
		this.targetDirectorySuffix = targetDirectorySuffix;
	}

}
