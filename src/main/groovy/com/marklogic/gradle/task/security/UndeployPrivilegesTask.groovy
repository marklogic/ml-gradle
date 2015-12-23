package com.marklogic.gradle.task.security

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class UndeployPrivilegesTask extends MarkLogicTask {

    @TaskAction
    void undeployPrivileges() {
        undeployWithCommandWithClassName("DeployPrivilegesCommand")
    }
}

