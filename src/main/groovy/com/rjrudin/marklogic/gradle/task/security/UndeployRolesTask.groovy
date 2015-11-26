package com.rjrudin.marklogic.gradle.task.security

import org.gradle.api.tasks.TaskAction

import com.rjrudin.marklogic.gradle.task.MarkLogicTask

class UndeployRolesTask extends MarkLogicTask {

    @TaskAction
    void undeployRoles() {
        undeployWithCommandWithClassName("DeployRolesCommand")
    }
}
