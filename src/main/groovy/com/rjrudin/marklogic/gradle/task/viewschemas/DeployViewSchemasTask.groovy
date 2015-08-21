package com.rjrudin.marklogic.gradle.task.viewschemas

import org.gradle.api.tasks.TaskAction

import com.rjrudin.marklogic.appdeployer.command.Command
import com.rjrudin.marklogic.gradle.task.MarkLogicTask

class DeployViewSchemasTask extends MarkLogicTask {

    @TaskAction
    void deployViewSchemas() {
        deployWithCommandListProperty("mlViewCommands")
    }
}
