package com.marklogic.gradle.task.schemasdata

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.AppDeployer
import com.marklogic.appdeployer.command.schemas.LoadSchemasDataCommand
import com.marklogic.appdeployer.impl.SimpleAppDeployer
import com.marklogic.gradle.task.MarkLogicTask

class LoadSchemasDataTask extends MarkLogicTask {

    @TaskAction
    void loadSchemasData() {
       LoadSchemasDataCommand command = null
        // Check for a LoadSchemasDataCommand in the AppDeployer first, as that may have additional configuration that we
        // don't want to have to duplicate on this task
        AppDeployer d = getAppDeployer()
        if (d instanceof SimpleAppDeployer) {
            command = d.getCommand("LoadSchemasDataCommand")
        }
        if (command == null) {
            command = new LoadSchemasDataCommand()
        }
        command.execute(getCommandContext())
    }
}