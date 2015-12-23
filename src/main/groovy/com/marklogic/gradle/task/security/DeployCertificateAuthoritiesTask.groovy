package com.marklogic.gradle.task.security

import org.gradle.api.tasks.TaskAction;

import com.marklogic.gradle.task.MarkLogicTask;

class DeployCertificateAuthoritiesTask extends MarkLogicTask {

    @TaskAction
    void deployCertificateAuthorities() {
        invokeDeployerCommandWithClassName("DeployCertificateAuthoritiesCommand")
    }
    
}