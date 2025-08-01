/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.tasks

import com.marklogic.appdeployer.command.tasks.WaitForTaskServerCommand
import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * Waits until the task server has no requests in progress.
 */
class WaitForTaskServerTask extends MarkLogicTask {

	@Input
	@Optional
	String groupName

	@Input
	int retryInSeconds = 0

	@TaskAction
	void waitForTaskServer() {
		WaitForTaskServerCommand c = new WaitForTaskServerCommand()
		if (groupName != null) {
			c.setGroupName(groupName)
		}
		if (retryInSeconds > 0) {
			c.setRetryInSeconds(retryInSeconds)
		}
		c.execute(getCommandContext())
	}
}
