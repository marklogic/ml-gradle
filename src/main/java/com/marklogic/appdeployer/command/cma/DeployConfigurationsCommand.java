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
package com.marklogic.appdeployer.command.cma;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.ResourceReference;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.mgmt.cma.ConfigurationManager;

import java.io.File;

/**
 * The /manage/v3 docs indicate that zips can be uploaded, but it's not clear what the format of those zips should be.
 * So for now, this just supports JSON and XML, which seem like the most likely formats to use.
 */
public class DeployConfigurationsCommand extends AbstractCommand {

	@Override
	public void execute(CommandContext context) {
		ConfigurationManager mgr = new ConfigurationManager(context.getManageClient());

		for (ConfigDir configDir : context.getAppConfig().getConfigDirs()) {
			File dir = configDir.getConfigurationsDir();
			if (dir != null && dir.exists()) {
				if (logger.isInfoEnabled()) {
					logger.info("Processing files in directory: " + dir.getAbsolutePath());
				}
				for (File f : super.listFilesInDirectory(dir)) {
					if (logger.isInfoEnabled()) {
						logger.info("Processing file: " + f.getAbsolutePath());
					}
					String payload = readResourceFromFile(context, f);
					SaveReceipt receipt = mgr.save(payload);
					afterResourceSaved(null, context, new ResourceReference(f, null), receipt);
				}
			} else {
				logResourceDirectoryNotFound(dir);
			}
		}
	}
}
