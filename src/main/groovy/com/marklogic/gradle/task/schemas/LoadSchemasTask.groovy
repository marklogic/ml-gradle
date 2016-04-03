package com.marklogic.gradle.task.schemas

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.AppDeployer
import com.marklogic.appdeployer.command.schemas.LoadSchemasCommand
import com.marklogic.appdeployer.impl.SimpleAppDeployer
import com.marklogic.gradle.task.MarkLogicTask

class LoadSchemasTask extends MarkLogicTask {

    @TaskAction
    void loadSchemas() {
       LoadSchemasCommand command = null
        // Check for a LoadSchemasCommand in the AppDeployer first, as that may have additional configuration that we
        // don't want to have to duplicate on this task
        AppDeployer d = getAppDeployer()
        if (d instanceof SimpleAppDeployer) {
            command = d.getCommand("LoadSchemasCommand")
        }
        if (command == null) {
            command = new LoadSchemasCommand()
        }
        command.execute(getCommandContext())
    }
}
