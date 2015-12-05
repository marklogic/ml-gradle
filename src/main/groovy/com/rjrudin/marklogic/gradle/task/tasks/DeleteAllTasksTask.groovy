package com.rjrudin.marklogic.gradle.task.tasks

import org.gradle.api.tasks.TaskAction

import com.rjrudin.marklogic.gradle.task.MarkLogicTask
import com.rjrudin.marklogic.mgmt.tasks.TaskManager

class DeleteAllTasksTask extends MarkLogicTask {

    @TaskAction
    void deleteAllTasks() {
        new TaskManager(getManageClient()).deleteAllScheduledTasks()
    }
}
