package com.rjrudin.marklogic.gradle.task.security

import org.gradle.api.tasks.TaskAction

import com.rjrudin.marklogic.gradle.task.MarkLogicTask

class UndeploySecurityTask extends MarkLogicTask {

    @TaskAction
    void undeploySecurity() {
        undeployWithCommandListProperty("mlSecurityCommands")
    }
}