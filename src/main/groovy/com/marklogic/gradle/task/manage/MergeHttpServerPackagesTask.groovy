package com.marklogic.gradle.task.manage

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class MergeHttpServerPackagesTask extends MarkLogicTask {

    @TaskAction
    void mergeHttpServerPackages() {
        println "Merging HTTP server packages"
        getAppDeployer().mergeHttpServerPackages(getAppConfig())
        println "Finished merging HTTP server packages"
    }
}
