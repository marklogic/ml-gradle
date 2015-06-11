package com.marklogic.gradle.task.client

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.command.modules.LoadModulesCommand
import com.marklogic.gradle.task.MarkLogicTask

class LoadModulesTask extends MarkLogicTask {

    String assetRolesAndCapabilities

    @TaskAction
    void loadModules() {
        println "Loading modules from paths: " + getAppConfig().getModulePaths()
        LoadModulesCommand c = new LoadModulesCommand()
        if (assetRolesAndCapabilities != null) {
            c.setAssetRolesAndCapabilities(assetRolesAndCapabilities)
        }
        c.execute(getCommandContext())
    }
}
