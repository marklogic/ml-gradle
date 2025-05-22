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
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.ResourceFilenameFilter;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.temporal.TemporalCollectionLSQTManager;

import java.io.File;

public class DeployTemporalCollectionsLSQTCommand extends AbstractCommand {

	public DeployTemporalCollectionsLSQTCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_TEMPORAL_COLLECTIONS_LSQT);
	}

	@Override
	public void execute(CommandContext context) {
		AppConfig appConfig = context.getAppConfig();
		for (ConfigDir configDir : appConfig.getConfigDirs()) {
			deployTemporalCollectionsLsqt(context, configDir, appConfig.getContentDatabaseName());
			for (File dir : configDir.getDatabaseResourceDirectories()) {
				String databaseName = determineDatabaseNameForDatabaseResourceDirectory(context, configDir, dir);
				if (databaseName != null) {
					deployTemporalCollectionsLsqt(context, new ConfigDir(dir), databaseName);
				}
			}
		}
	}

	protected void deployTemporalCollectionsLsqt(CommandContext context, ConfigDir configDir, String databaseIdOrName) {
		File dir = configDir.getTemporalCollectionsLsqtDir();
		if (dir != null && dir.exists()) {
			File[] files = dir.listFiles(new ResourceFilenameFilter());
			if (files != null) {
				for (File f : files) {
					String name = f.getName();
					String temporalCollectionName = makeTemporalCollectionName(name);
					String payload = copyFileToString(f, context);
					if (logger.isInfoEnabled()) {
						logger.info(format("Extracted temporal collection name '%s' from filename '%s'", temporalCollectionName, name));
					}
					new TemporalCollectionLSQTManager(context.getManageClient(), databaseIdOrName, temporalCollectionName).save(payload);
				}
			}
		} else {
			logResourceDirectoryNotFound(dir);
		}
	}

	private String makeTemporalCollectionName(String filename) {
		// use filename without suffix as temporal collection
		return filename.endsWith(".xml") || filename.endsWith(".json") ?
			filename.substring(0, filename.length() - 4) : filename;
	}
}
