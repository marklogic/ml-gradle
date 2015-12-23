package com.marklogic.gradle.task.security

import org.gradle.api.tasks.TaskAction;

import com.marklogic.gradle.task.MarkLogicTask;

class DeployUsersTask extends MarkLogicTask {

    @TaskAction
    void deployUsers() {
        invokeDeployerCommandWithClassName("DeployUsersCommand")
    }
}