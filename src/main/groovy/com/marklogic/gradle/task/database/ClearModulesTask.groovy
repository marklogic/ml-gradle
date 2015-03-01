package com.marklogic.gradle.task.database

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class ClearModulesTask extends MarkLogicTask {

    String[] excludes

    @TaskAction
    void clearModules() {
        println "Clearing modules database"
        getAppDeployer().clearModulesDatabase(getAppConfig(), excludes)
        println "Finished clearing modules database"
    }
}
