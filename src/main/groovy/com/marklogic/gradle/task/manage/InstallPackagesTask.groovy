package com.marklogic.gradle.task.manage

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.AppConfig
import com.marklogic.gradle.task.MarkLogicTask

class InstallPackagesTask extends MarkLogicTask {

    @TaskAction
    void installPackages() {
        println "Installing database and appserver packages"
        getAppDeployer().installPackages(getAppConfig())
        println "Finished installing packages"
    }
}
