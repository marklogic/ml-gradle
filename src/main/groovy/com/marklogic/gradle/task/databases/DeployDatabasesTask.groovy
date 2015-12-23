package com.marklogic.gradle.task.databases

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class DeployDatabasesTask extends MarkLogicTask {

    @TaskAction
    void deployDatabases() {
        deployWithCommandListProperty("mlDatabaseCommands")
    }
}
