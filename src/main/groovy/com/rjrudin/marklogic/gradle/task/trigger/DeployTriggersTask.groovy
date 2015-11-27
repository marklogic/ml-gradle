package com.rjrudin.marklogic.gradle.task.trigger

import org.gradle.api.tasks.TaskAction

import com.rjrudin.marklogic.gradle.task.MarkLogicTask

class DeployTriggersTask extends MarkLogicTask {

    @TaskAction
    void deployTriggers() {
        deployWithCommandListProperty("mlTriggerCommands")
    }
}