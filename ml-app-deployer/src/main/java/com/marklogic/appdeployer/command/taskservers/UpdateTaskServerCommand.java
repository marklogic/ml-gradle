/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
