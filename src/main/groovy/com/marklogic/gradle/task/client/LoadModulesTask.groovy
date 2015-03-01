package com.marklogic.gradle.task.client

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.AppConfig
import com.marklogic.gradle.task.MarkLogicTask

class LoadModulesTask extends MarkLogicTask {

    String assetRolesAndCapabilities

    @TaskAction
    void loadModules() {
        getAppDeployer().loadModules(getAppConfig(), assetRolesAndCapabilities)
    }
}
