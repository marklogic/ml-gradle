package com.marklogic.gradle.task.flexrep

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class DeployFlexrepAtPathTask extends MarkLogicTask {

    @TaskAction
    void deployFlexrepAtPath() {
        invokeDeployerCommandWithClassName("DeployFlexrepCommand")
    }
}
