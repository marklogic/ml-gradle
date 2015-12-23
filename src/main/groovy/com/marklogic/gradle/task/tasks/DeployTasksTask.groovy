package com.marklogic.gradle.task.tasks

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.command.Command
import com.marklogic.gradle.task.MarkLogicTask

class DeployTasksTask extends MarkLogicTask {

    @TaskAction
    void deployTasks() {
        deployWithCommandListProperty("mlTaskCommands")
    }
}