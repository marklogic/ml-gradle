package com.marklogic.gradle.task.databases

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.databases.DatabaseManager

class MergeDatabaseTask extends MarkLogicTask {

    @TaskAction
    void clearModules() {
        if (project.hasProperty("dbName")) {
            def dbName = project.property("dbName")
            println "Merging database " + dbName
            DatabaseManager mgr = new DatabaseManager(getManageClient())
            mgr.mergeDatabase(dbName)
            println "Finished merging database " + dbName
        } else {
            println "To merge a the database, include the dbName parameter e.g. -PdbName=my-database"
            return
        }
    }
}
