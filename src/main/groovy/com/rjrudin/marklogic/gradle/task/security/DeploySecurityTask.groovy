package com.rjrudin.marklogic.gradle.task.security

import org.gradle.api.tasks.TaskAction

import com.rjrudin.marklogic.appdeployer.command.Command
import com.rjrudin.marklogic.gradle.task.MarkLogicTask

class DeploySecurityTask extends MarkLogicTask {

    @TaskAction
    void deploySecurity() {
        deployWithCommandListProperty("mlSecurityCommands")
    }
}
