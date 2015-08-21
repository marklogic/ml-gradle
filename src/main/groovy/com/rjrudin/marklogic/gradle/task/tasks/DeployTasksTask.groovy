package com.rjrudin.marklogic.gradle.task.tasks

import org.gradle.api.tasks.TaskAction

import com.rjrudin.marklogic.appdeployer.command.Command
import com.rjrudin.marklogic.gradle.task.MarkLogicTask

class DeployTasksTask extends MarkLogicTask {

    @TaskAction
    void deployTasks() {
        deployWithCommandListProperty("mlTaskCommands")
    }
}