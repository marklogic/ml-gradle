package com.marklogic.gradle.task.alert

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class DeployAlertingTask extends MarkLogicTask {

    @TaskAction
    void deployAlerting() {
        deployWithCommandListProperty("mlAlertCommands")
    }
}
