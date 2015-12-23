package com.marklogic.gradle.task.flexrep

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class DeployFlexrepTask extends MarkLogicTask {

    @TaskAction
    void deployFlexrep() {
        deployWithCommandListProperty("mlFlexrepCommands")
    }
}