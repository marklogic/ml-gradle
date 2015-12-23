package com.marklogic.gradle.task.tasks

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.tasks.TaskManager

class DeleteAllTasksTask extends MarkLogicTask {

    @TaskAction
    void deleteAllTasks() {
        new TaskManager(getManageClient()).deleteAllScheduledTasks()
    }
}
