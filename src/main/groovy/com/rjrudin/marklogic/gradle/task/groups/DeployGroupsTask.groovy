package com.rjrudin.marklogic.gradle.task.groups

import org.gradle.api.tasks.TaskAction

import com.rjrudin.marklogic.appdeployer.command.Command
import com.rjrudin.marklogic.gradle.task.MarkLogicTask

class DeployGroupsTask extends MarkLogicTask {

    @TaskAction
    void deployGroups() {
        deployWithCommandListProperty("mlGroupCommands")
    }
}
