package com.marklogic.gradle.task.security

import org.gradle.api.tasks.TaskAction;

import com.marklogic.gradle.task.MarkLogicTask;

class DeployProtectedCollectionsTask extends MarkLogicTask {

    @TaskAction
    void deployProtectedCollections() {
        invokeDeployerCommandWithClassName("DeployProtectedCollectionsCommand")
    }
    
}
