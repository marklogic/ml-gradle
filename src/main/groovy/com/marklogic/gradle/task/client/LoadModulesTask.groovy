package com.marklogic.gradle.task.client

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.plugin.modules.LoadModulesPlugin
import com.marklogic.gradle.task.MarkLogicTask

class LoadModulesTask extends MarkLogicTask {

    // TODO Bake this into the plugin?
    // String assetRolesAndCapabilities

    @TaskAction
    void loadModules() {
        println "Loading modules from paths: " + getAppConfig().getModulePaths()

        new LoadModulesPlugin().onDeploy(getAppPluginContext())
    }
}
