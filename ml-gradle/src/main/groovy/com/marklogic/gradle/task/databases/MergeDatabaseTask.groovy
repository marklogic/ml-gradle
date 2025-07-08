/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.databases

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.resource.databases.DatabaseManager

class MergeDatabaseTask extends MarkLogicTask {

    @TaskAction
    void mergeDatabase() {
        if (project.hasProperty("dbName")) {
            def dbName = project.property("dbName")
            println "Sending request to merge database " + dbName
            DatabaseManager mgr = new DatabaseManager(getManageClient())
            mgr.mergeDatabase(dbName)
            println "Finished sending request to merge database " + dbName
        } else {
            println "To merge a the database, include the dbName parameter e.g. -PdbName=my-database"
            return
        }
    }
}
