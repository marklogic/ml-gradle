package com.marklogic.gradle.task.database

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class ClearContentDatabaseTask extends MarkLogicTask {

    @TaskAction
    void clearModules() {
        if (project.hasProperty("collection")) {
            println "Clearing documents in collection: " + project.property("collection")
            getAppDeployer().clearContentDatabase(getAppConfig(), project.property("collection"))
            println "Finished clearing documents in collection: " + project.property("collection")
        } else if (project.hasProperty("deleteAll")) {
            println "Clearing all documents in content database"
            getAppDeployer().clearContentDatabase(getAppConfig(), null)
            println "Finished clearing all documents in content database"
        } else {
            println "To delete the documents in one collection, specify a collection via -Pcollection=name."
            println "To delete all documents in the database, include the deleteAll parameter e.g. -PdeleteAll=true"
            return
        }
    }
}
