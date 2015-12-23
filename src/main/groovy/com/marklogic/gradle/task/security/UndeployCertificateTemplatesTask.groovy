package com.marklogic.gradle.task.security

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class UndeployCertificateTemplatesTask extends MarkLogicTask {

    @TaskAction
    void undeployCertificateTemplates() {
        undeployWithCommandWithClassName("DeployCertificateTemplatesCommand")
    }
}
