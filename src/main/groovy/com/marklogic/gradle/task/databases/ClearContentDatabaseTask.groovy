package com.marklogic.gradle.task.databases

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask
import com.rjrudin.marklogic.mgmt.databases.DatabaseManager

class ClearContentDatabaseTask extends MarkLogicTask {

    @TaskAction
    void clearModules() {
        if (project.hasProperty("deleteAll")) {
            println "Clearing all documents in content database"
            DatabaseManager mgr = new DatabaseManager(getManageClient())
            mgr.clearDatabase(getAppConfig().getContentDatabaseName())
            println "Finished clearing all documents in content database"
        } else {
            println "To clear the database, include the deleteAll parameter e.g. -PdeleteAll=true"
            return
        }
    }
}
