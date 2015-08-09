package com.rjrudin.marklogic.gradle.task

import org.gradle.api.tasks.TaskAction

class DeployAppTask extends MarkLogicTask {

    @TaskAction
    void deployApp() {
        getAppDeployer().deploy(getAppConfig())
    }
}
