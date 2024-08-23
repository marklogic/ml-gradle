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
		for (ConfigDir configDir : appConfig.getConfigDirs()) {
			deployTemporalAxes(context, configDir, appConfig.getContentDatabaseName());
			for (File dir : configDir.getDatabaseResourceDirectories()) {
				String databaseName = determineDatabaseNameForDatabaseResourceDirectory(context, configDir, dir);
				if (databaseName != null) {
					deployTemporalAxes(context, new ConfigDir(dir), databaseName);
				}
			}
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
