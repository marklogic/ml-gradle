package com.marklogic.gradle.task.tasks

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.resource.tasks.TaskManager
import org.gradle.api.tasks.TaskAction

class DeleteAllTasksTask extends MarkLogicTask {

    @TaskAction
    void deleteAllTasks() {
        new TaskManager(getManageClient()).deleteAllScheduledTasks()
    }
}
