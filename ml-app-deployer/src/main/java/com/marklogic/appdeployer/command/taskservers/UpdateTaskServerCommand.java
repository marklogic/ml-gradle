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
package com.marklogic.appdeployer.command.taskservers;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.taskservers.TaskServerManager;

import java.io.File;

/**
 * As of ML 9.0-3, the Manage API only supports a PUT call to update the single task server within a cluster.
 * <p>
 * In addition, the single task server is named "TaskServer" and there's no apparent way to change that. So this
 * command assumes that name as well.
 */
public class UpdateTaskServerCommand extends AbstractCommand {

	private String taskServerName = "TaskServer";

	public UpdateTaskServerCommand() {
		setExecuteSortOrder(SortOrderConstants.UPDATE_TASK_SERVER);
	}

	@Override
	public void execute(CommandContext context) {
		for (ConfigDir configDir : context.getAppConfig().getConfigDirs()) {
			File dir = configDir.getTaskServersDir();
			if (dir.exists()) {
				if (logger.isInfoEnabled()) {
					logger.info("Processing files in directory: " + dir.getAbsolutePath());
				}

				TaskServerManager mgr = new TaskServerManager(context.getManageClient());
				for (File f : listFilesInDirectory(dir)) {
					if (logger.isInfoEnabled()) {
						logger.info("Processing file: " + f.getAbsolutePath());
					}
					String payload = copyFileToString(f, context);
					mgr.updateTaskServer(taskServerName, payload, context.getAdminManager());
				}
			} else {
				logResourceDirectoryNotFound(dir);
			}
		}
	}

	public void setTaskServerName(String taskServerName) {
		this.taskServerName = taskServerName;
	}
}
