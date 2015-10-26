package com.rjrudin.marklogic.gradle.task.security

import org.gradle.api.tasks.TaskAction;

import com.rjrudin.marklogic.gradle.task.MarkLogicTask;

class DeployExternalSecurityTask extends MarkLogicTask {

    @TaskAction
    void deployExternalSecurity() {
        invokeDeployerCommandWithClassName("DeployExternalSecurityCommand")
    }
    
}
