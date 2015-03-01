package com.marklogic.gradle.task.manage

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class UpdateHttpServerTask extends MarkLogicTask {

    @TaskAction
    void updateHttpServers() {
        getAppDeployer().updateHttpServers(getAppConfig())
    }
}
