package com.marklogic.gradle.task.viewschemas

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.command.Command
import com.marklogic.gradle.task.MarkLogicTask

class DeployViewSchemasTask extends MarkLogicTask {

    @TaskAction
    void deployViewSchemas() {
        deployWithCommandListProperty("mlViewCommands")
    }
}
