package com.marklogic.gradle.task.security

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class UndeployAmpsTask extends MarkLogicTask {

    @TaskAction
    void undeployAmps() {
        undeployWithCommandWithClassName("DeployAmpsCommand")
    }
}

