/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.databases

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.resource.databases.DatabaseManager

class MergeContentDatabaseTask extends MarkLogicTask {

    @TaskAction
    void mergeContentDatabase() {
        def dbName = getAppConfig().getContentDatabaseName()
        println "Sending request to merge database " + dbName
        DatabaseManager mgr = new DatabaseManager(getManageClient())
        mgr.mergeDatabase(dbName)
        println "Finished sending request to merge database " + dbName
    }
}
