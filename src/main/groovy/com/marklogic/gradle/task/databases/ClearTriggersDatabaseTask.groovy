package com.marklogic.gradle.task.databases

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.databases.DatabaseManager

class ClearTriggersDatabaseTask extends MarkLogicTask {

    @TaskAction
    void clearModules() {
        println "Clearing all documents in triggers database"
        DatabaseManager mgr = new DatabaseManager(getManageClient())
        mgr.clearDatabase(getAppConfig().getTriggersDatabaseName())
        println "Finished clearing all documents in triggers database"
    }
}
