package com.rjrudin.marklogic.gradle.task.alert

import org.gradle.api.tasks.TaskAction

import com.rjrudin.marklogic.gradle.task.MarkLogicTask

class DeployAlertingTask extends MarkLogicTask {

    @TaskAction
    void deployAlerting() {
        deployWithCommandListProperty("mlAlertCommands")
    }
}
