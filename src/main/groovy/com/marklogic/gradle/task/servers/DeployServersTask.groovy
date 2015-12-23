package com.marklogic.gradle.task.servers

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.command.Command
import com.marklogic.gradle.task.MarkLogicTask

class DeployServersTask extends MarkLogicTask {

    @TaskAction
    void deployServers() {
        deployWithCommandListProperty("mlServerCommands")
    }
}
