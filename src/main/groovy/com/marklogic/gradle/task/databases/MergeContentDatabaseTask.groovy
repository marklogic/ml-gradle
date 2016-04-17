package com.marklogic.gradle.task.databases

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.databases.DatabaseManager

class MergeContentDatabaseTask extends MarkLogicTask {

    @TaskAction
    void clearModules() {
        def dbName = getAppConfig().getContentDatabaseName()
        println "Merging database " + dbName
        DatabaseManager mgr = new DatabaseManager(getManageClient())
        mgr.mergeDatabase(dbName)
        println "Finished merging database " + dbName
    }
}