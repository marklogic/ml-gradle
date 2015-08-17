package com.rjrudin.marklogic.gradle.task.client

import org.gradle.api.tasks.TaskAction

import com.rjrudin.marklogic.appdeployer.AppDeployer
import com.rjrudin.marklogic.appdeployer.command.modules.LoadModulesCommand
import com.rjrudin.marklogic.appdeployer.impl.SimpleAppDeployer
import com.rjrudin.marklogic.gradle.task.MarkLogicTask

class LoadModulesTask extends MarkLogicTask {

    @TaskAction
    void loadModules() {
        LoadModulesCommand command = null
        // Check for a LoadModulesCommand in the AppDeployer first, as that may have additional configuration that we
        // don't want to have to duplicate on this task
        AppDeployer d = getAppDeployer()
        if (d instanceof SimpleAppDeployer) {
            command = d.getCommand("LoadModulesCommand")
        }

        if (command == null) {
            command = new LoadModulesCommand()
            if (project.hasProperty("mlModulePermissions")) {
                command.setDefaultAssetRolesAndCapabilities(project.property("mlModulePermissions"))
            }
        }

        new LoadModulesCommand().execute(getCommandContext())
    }
}
