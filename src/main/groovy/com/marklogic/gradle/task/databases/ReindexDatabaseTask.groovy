package com.marklogic.gradle.task.databases

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.resource.databases.DatabaseManager

class ReindexDatabaseTask extends MarkLogicTask {

    @TaskAction
    void reindexDatabase() {
        if (project.hasProperty("dbName")) {
            def dbName = project.property("dbName")
            println "Sending request to reindex database " + dbName
            DatabaseManager mgr = new DatabaseManager(getManageClient())
            mgr.reindexDatabase(dbName)
            println "Finished sending request to reindex database " + dbName
        } else {
            println "To reindex a the database, include the dbName parameter e.g. -PdbName=my-database"
            return
        }
    }
}
