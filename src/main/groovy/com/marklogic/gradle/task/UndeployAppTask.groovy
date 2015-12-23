package com.marklogic.gradle.task

import org.gradle.api.tasks.TaskAction

class UndeployAppTask extends MarkLogicTask {

    @TaskAction
    void deployApp() {
        getAppDeployer().undeploy(getAppConfig())
    }
}
