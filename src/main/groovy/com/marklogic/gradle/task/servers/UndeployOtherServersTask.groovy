package com.marklogic.gradle.task.servers

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class UndeployOtherServersTask extends MarkLogicTask {

    @TaskAction
    void undeployOtherServers() {
        undeployWithCommandWithClassName("DeployOtherServersCommand")
    }
}
