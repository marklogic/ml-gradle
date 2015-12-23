package com.marklogic.gradle.task.groups

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.command.Command
import com.marklogic.gradle.task.MarkLogicTask

class DeployGroupsTask extends MarkLogicTask {

    @TaskAction
    void deployGroups() {
        deployWithCommandListProperty("mlGroupCommands")
    }
}
