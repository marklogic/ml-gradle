package com.marklogic.gradle.task.security

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.command.Command
import com.marklogic.gradle.task.MarkLogicTask

class DeploySecurityTask extends MarkLogicTask {

    @TaskAction
    void deploySecurity() {
        deployWithCommandListProperty("mlSecurityCommands")
    }
}
