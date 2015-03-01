package com.marklogic.gradle.task

import org.gradle.api.tasks.TaskAction

class UninstallAppTask extends MarkLogicTask {

    @TaskAction
    void uninstallApp() {
        newAppDeployer().uninstallApp(getAppConfig())
    }
}
