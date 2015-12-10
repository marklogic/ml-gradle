package com.rjrudin.marklogic.gradle.task.flexrep

import org.gradle.api.tasks.TaskAction

import com.rjrudin.marklogic.gradle.task.MarkLogicTask
import com.rjrudin.marklogic.mgmt.flexrep.ConfigManager

class DisableAllFlexrepTargetsTask extends MarkLogicTask {

    @TaskAction
    void disableAllFlexrepTargets() {
        new ConfigManager(getManageClient(), getAppConfig().getContentDatabaseName()).disableAllFlexrepTargets()
    }
}