package com.marklogic.gradle.task.tasks

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.resource.tasks.TaskManager
import org.gradle.api.tasks.TaskAction

class EnableAllTasksTask extends MarkLogicTask {

	@TaskAction
	void enableAllTasks() {
		String group = project.hasProperty("mlGroupName") ? project.property("mlGroupName") : "Default"
		println "Enabling all scheduled tasks in group: " + group
		new TaskManager(getManageClient(), group).enableAllTasks()
		println "Finished enabling all scheduled tasks in group: " + group
	}
}
