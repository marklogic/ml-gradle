package com.marklogic.gradle.task.manage

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class UpdateHttpServerTask extends MarkLogicTask {

    @TaskAction
    void updateHttpServers() {
        println "Updating HTTP servers"
        getAppDeployer().updateHttpServers(getAppConfig())
        println "Finished updating HTTP servers"
    }
}
