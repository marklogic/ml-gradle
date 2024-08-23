/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
				String databaseName = determineDatabaseNameForDatabaseResourceDirectory(context, configDir, dir);
				if (databaseName != null) {
					deployActions(context, new ConfigDir(dir), databaseName);
				}
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
		} else {
			logResourceDirectoryNotFound(configsDir);
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
