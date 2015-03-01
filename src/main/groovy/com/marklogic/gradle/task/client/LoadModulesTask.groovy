package com.marklogic.gradle.task.client

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.AppConfig
import com.marklogic.gradle.task.MarkLogicTask

class LoadModulesTask extends MarkLogicTask {

    List<String> modulePaths
    String assetRolesAndCapabilities

    @TaskAction
    void loadModules() {
        AppConfig config = getAppConfig()
        config.setModulePaths(modulePaths)
        newAppDeployer().loadModules(config, assetRolesAndCapabilities)
    }
}
