package com.marklogic.gradle.task.flexrep

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.flexrep.ConfigManager

class DisableAllFlexrepTargetsTask extends MarkLogicTask {

    @TaskAction
    void disableAllFlexrepTargets() {
        new ConfigManager(getManageClient(), getAppConfig().getContentDatabaseName()).disableAllFlexrepTargets()
    }
}