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
package com.marklogic.appdeployer.command.triggers;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.triggers.TriggerManager;

import java.io.File;

/**
 * Defaults to the triggers database name in the AppConfig instance. Can be overridden via the databaseNameOrId
 * property.
 *
 * As of version 3.7.0, this now supports a triggers "database resource directory" under "databases/(name of triggers database)/triggers".
 */
public class DeployTriggersCommand extends AbstractResourceCommand {

    private String databaseIdOrName;
    private TriggerManager currentTriggerManager;

    public DeployTriggersCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_TRIGGERS);
        // Triggers are stored in a database, so we don't need to delete them as the database will be deleted
        setDeleteResourcesOnUndo(false);
    }

	@Override
	public void execute(CommandContext context) {
		AppConfig appConfig = context.getAppConfig();
		for (ConfigDir configDir : appConfig.getConfigDirs()) {
			final String initialTriggersDatabaseName = databaseIdOrName != null ? databaseIdOrName : appConfig.getTriggersDatabaseName();
			deployTriggers(context, configDir, initialTriggersDatabaseName);
			for (File dir : configDir.getDatabaseResourceDirectories()) {
				String databaseName = determineDatabaseNameForDatabaseResourceDirectory(context, configDir, dir);
				if (databaseName != null) {
					deployTriggers(context, new ConfigDir(dir), databaseName);
				}
			}
		}
	}

	protected void deployTriggers(CommandContext context, ConfigDir configDir, String databaseIdOrName) {
		currentTriggerManager = new TriggerManager(context.getManageClient(), databaseIdOrName);
		processExecuteOnResourceDir(context, configDir.getTriggersDir());
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return currentTriggerManager;
	}

	@Override
    protected File[] getResourceDirs(CommandContext context) {
    	return findResourceDirs(context, configDir -> configDir.getTriggersDir());
    }

    public void setDatabaseIdOrName(String databaseIdOrName) {
        this.databaseIdOrName = databaseIdOrName;
    }

}
