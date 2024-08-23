/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
