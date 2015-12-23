package com.marklogic.gradle.task.security

import org.gradle.api.tasks.TaskAction;

import com.marklogic.gradle.task.MarkLogicTask;

class DeployCertificateTemplatesTask extends MarkLogicTask {

    @TaskAction
    void deployCertificateTemplates() {
        invokeDeployerCommandWithClassName("DeployCertificateTemplatesCommand")
    }
    
}