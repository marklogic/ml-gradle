package com.marklogic.gradle.task.client

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.AppConfig
import com.marklogic.gradle.task.MarkLogicTask

class LoadModulesTask extends MarkLogicTask {

    String assetRolesAndCapabilities

    @TaskAction
    void loadModules() {
        println "Loading modules from paths: " + getAppConfig().getModulePaths()
        getAppDeployer().loadModules(getAppConfig(), assetRolesAndCapabilities)
    }
}
