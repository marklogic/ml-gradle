package com.marklogic.gradle.task.databases

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.resource.databases.DatabaseManager

class ReindexContentDatabaseTask extends MarkLogicTask {

    @TaskAction
    void reindexContentDatabase() {
        def dbName = getAppConfig().getContentDatabaseName()
        println "Sending request to reindex database " + dbName
        DatabaseManager mgr = new DatabaseManager(getManageClient())
        mgr.reindexDatabase(dbName)
        println "Finished sending request to reindex database " + dbName
    }
}
