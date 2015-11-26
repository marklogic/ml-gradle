package com.rjrudin.marklogic.gradle.task.security

import org.gradle.api.tasks.TaskAction;

import com.rjrudin.marklogic.gradle.task.MarkLogicTask;

class UndeployProtectedCollectionsTask extends MarkLogicTask {

    @TaskAction
    void undeployProtectedCollections() {
        undeployWithCommandWithClassName("DeployProtectedCollectionsCommand")
    }
}

