package com.marklogic.gradle.task.security

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class UndeployExternalSecurityTask extends MarkLogicTask {

    @TaskAction
    void undeployExternalSecurity() {
        undeployWithCommandWithClassName("DeployExternalSecurityCommand")
    }
}


