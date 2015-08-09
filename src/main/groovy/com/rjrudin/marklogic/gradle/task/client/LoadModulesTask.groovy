package com.rjrudin.marklogic.gradle.task.client

import org.gradle.api.tasks.TaskAction

import com.rjrudin.marklogic.appdeployer.AppDeployer
import com.rjrudin.marklogic.appdeployer.command.Command
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
            List<Command> commands = ((SimpleAppDeployer)d).getCommands()
            for (Command c : commands) {
                if (c instanceof LoadModulesCommand) {
                    command = c
                    break
                }
            }
        }

        if (command == null) {
            command = new LoadModulesCommand()
        }

        new LoadModulesCommand().execute(getCommandContext())
    }
}
