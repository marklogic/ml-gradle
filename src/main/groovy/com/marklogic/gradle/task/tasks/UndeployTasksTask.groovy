package com.marklogic.gradle.task.tasks

import org.gradle.api.tasks.TaskAction;

import com.marklogic.gradle.task.MarkLogicTask;

class UndeployTasksTask extends MarkLogicTask {

    @TaskAction
    void undeployTasks() {
        undeployWithCommandListProperty("mlTaskCommands")
    }
}
