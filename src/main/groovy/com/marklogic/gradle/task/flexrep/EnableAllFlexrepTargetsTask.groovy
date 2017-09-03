package com.marklogic.gradle.task.flexrep

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.resource.flexrep.ConfigManager

class EnableAllFlexrepTargetsTask extends MarkLogicTask {

    @TaskAction
    void enableAllFlexrepTargets() {
        new ConfigManager(getManageClient(), getAppConfig().getContentDatabaseName()).enableAllFlexrepTargets()
    }
}
