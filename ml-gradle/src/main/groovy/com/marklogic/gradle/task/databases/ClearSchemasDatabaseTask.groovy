/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.databases

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.resource.databases.DatabaseManager

class ClearSchemasDatabaseTask extends MarkLogicTask {

    @TaskAction
    void clearSchemasDatabase() {
        println "Clearing all documents in schemas database"
        DatabaseManager mgr = new DatabaseManager(getManageClient())
        mgr.clearDatabase(getAppConfig().getSchemasDatabaseName())
        println "Finished clearing all documents in schemas database"
    }
}
