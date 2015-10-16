package com.rjrudin.marklogic.gradle.task.forests

import org.gradle.api.tasks.TaskAction

import com.rjrudin.marklogic.gradle.task.MarkLogicTask

class DeployForestReplicasTask extends MarkLogicTask {

    @TaskAction
    void deployForestReplicas() {
        deployWithCommandListProperty("mlForestReplicaCommands")
    }
}
