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
package com.marklogic.appdeployer.command.rebalancer;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.rebalancer.PartitionQueryManager;

import java.io.File;

public class DeployPartitionQueriesCommand extends AbstractResourceCommand {

	private PartitionQueryManager currentPartitionQueryManager;

	public DeployPartitionQueriesCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_PARTITION_QUERIES);

		// Partition queries are expected to be deleted when the parent database is deleted
		setDeleteResourcesOnUndo(false);
	}

	@Override
	public void execute(CommandContext context) {
		AppConfig appConfig = context.getAppConfig();
		for (ConfigDir configDir : appConfig.getConfigDirs()) {
			for (File dir : configDir.getDatabaseResourceDirectories()) {
				String databaseName = determineDatabaseNameForDatabaseResourceDirectory(context, configDir, dir);
				if (databaseName != null) {
					deployPartitionQueries(context, new ConfigDir(dir), databaseName);
				}
			}
		}
	}

	protected void deployPartitionQueries(CommandContext context, ConfigDir configDir, String databaseIdOrName) {
		currentPartitionQueryManager = new PartitionQueryManager(context.getManageClient(), databaseIdOrName);
		processExecuteOnResourceDir(context, configDir.getPartitionQueriesDir());
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return currentPartitionQueryManager;
	}

	@Override
	protected File[] getResourceDirs(CommandContext context) {
		return findResourceDirs(context, configDir -> configDir.getPartitionQueriesDir());
	}
}
