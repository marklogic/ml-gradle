package com.marklogic.gradle.task.manage

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class MergeHttpServerPackagesTask extends MarkLogicTask {

    @TaskAction
    void mergeHttpServerPackages() {
        getAppDeployer().mergeHttpServerPackages(getAppConfig())
    }
}
