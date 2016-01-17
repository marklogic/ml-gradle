package com.marklogic.gradle.task.mimetypes

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class DeployMimetypesTask extends MarkLogicTask {

    @TaskAction
    void deployMimetypes() {
        deployWithCommandListProperty("mlMimetypeCommands")
    }
}
