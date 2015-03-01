package com.marklogic.gradle.task.database

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class ClearContentDatabaseTask extends MarkLogicTask {

    @TaskAction
    void clearModules() {
        if (project.hasProperty("collection")) {
            newAppDeployer().clearContentDatabase(getAppConfig(), project.property("collection"))
        } else if (project.hasProperty("deleteAll")) {
            newAppDeployer().clearContentDatabase(getAppConfig(), null)
        } else {
            println "To delete the documents in one collection, specify a collection via -Pcollection=name."
            println "To delete all documents in the database, include the deleteAll parameter e.g. -PdeleteAll=true"
            return
        }
    }
}
