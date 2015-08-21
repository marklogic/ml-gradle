package com.rjrudin.marklogic.gradle.task.servers

import org.gradle.api.tasks.TaskAction

import com.rjrudin.marklogic.appdeployer.command.Command
import com.rjrudin.marklogic.gradle.task.MarkLogicTask

class DeployServersTask extends MarkLogicTask {

    @TaskAction
    void deployServers() {
        deployWithCommandListProperty("mlServerCommands")
    }
}
