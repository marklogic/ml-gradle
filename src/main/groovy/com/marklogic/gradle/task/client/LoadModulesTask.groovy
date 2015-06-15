package com.marklogic.gradle.task.client

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.command.modules.LoadModulesCommand
import com.marklogic.gradle.task.MarkLogicTask

class LoadModulesTask extends MarkLogicTask {

    @TaskAction
    void loadModules() {
        new LoadModulesCommand().execute(getCommandContext())
    }
}
