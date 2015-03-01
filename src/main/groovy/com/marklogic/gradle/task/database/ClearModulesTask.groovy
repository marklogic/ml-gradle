package com.marklogic.gradle.task.database

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class ClearModulesTask extends MarkLogicTask {

    String[] excludes

    @TaskAction
    void clearModules() {
        getAppDeployer().clearModulesDatabase(getAppConfig(), excludes)
    }
}
