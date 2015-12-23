package com.marklogic.gradle.task.security

import org.gradle.api.tasks.TaskAction;

import com.marklogic.gradle.task.MarkLogicTask;

class DeployAmpsTask extends MarkLogicTask {

    @TaskAction
    void deployAmps() {
        invokeDeployerCommandWithClassName("DeployAmpsCommand")
    }
}
