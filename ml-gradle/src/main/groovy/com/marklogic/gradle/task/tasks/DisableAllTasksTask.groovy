/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.tasks

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.resource.tasks.TaskManager
import org.gradle.api.tasks.TaskAction

class DisableAllTasksTask extends MarkLogicTask {

	@TaskAction
	void disableAllTasks() {
		String group = project.hasProperty("mlGroupName") ? project.property("mlGroupName") : "Default"
		println "Disabling all scheduled tasks in group: " + group
		new TaskManager(getManageClient(), group).disableAllTasks()
		println "Finished disabling all scheduled tasks in group: " + group
	}
}
