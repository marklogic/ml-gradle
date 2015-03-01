package com.marklogic.gradle.task.manage

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class UpdateDatabaseTask extends MarkLogicTask {

    @TaskAction
    void updateDatabase() {
        println "Updating content databases"
        getAppDeployer().updateContentDatabases(getAppConfig())
        println "Finished updating content databases"
    }
}
