/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.tasks

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.resource.tasks.TaskManager
import org.gradle.api.tasks.TaskAction

class DeleteAllTasksTask extends MarkLogicTask {

    @TaskAction
    void deleteAllTasks() {
	    String group = "Default"
	    if (project.hasProperty("mlGroupName")) {
		    group = project.property("mlGroupName")
	    }
	    println "Deleting all scheduled tasks in group: " + group
        new TaskManager(getManageClient()).deleteAllScheduledTasks()
    }
}
