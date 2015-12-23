package com.marklogic.gradle.task.security

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class UndeployUsersTask extends MarkLogicTask {

    @TaskAction
    void undeployUsers() {
        undeployWithCommandWithClassName("DeployUsersCommand")
    }
}