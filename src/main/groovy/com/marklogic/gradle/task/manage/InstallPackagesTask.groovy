package com.marklogic.gradle.task.manage

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.AppConfig
import com.marklogic.gradle.task.MarkLogicTask

class InstallPackagesTask extends MarkLogicTask {

    @TaskAction
    void installPackages() {
        newAppDeployer().installPackages(getAppConfig())
    }
}
