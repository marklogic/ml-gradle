/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.tasks;

import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.mgmt.resource.tasks.TaskManager;

public class WaitForTaskServerCommand extends AbstractCommand {

	private String groupName;
	private int retryInSeconds;

	@Override
	public void execute(CommandContext context) {
		TaskManager mgr = new TaskManager(context.getManageClient());
		String group = groupName != null ? groupName : context.getAppConfig().getGroupName();
		int retry = retryInSeconds > 0 ? retryInSeconds : 1;
		mgr.waitForTasksToComplete(group, retry);
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getRetryInSeconds() {
		return retryInSeconds;
	}

	public void setRetryInSeconds(int retryInSeconds) {
		this.retryInSeconds = retryInSeconds;
	}
}
