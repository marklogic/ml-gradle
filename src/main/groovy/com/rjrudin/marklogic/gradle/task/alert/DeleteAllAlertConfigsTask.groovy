package com.rjrudin.marklogic.gradle.task.alert

import org.gradle.api.tasks.TaskAction

import com.rjrudin.marklogic.gradle.task.MarkLogicTask
import com.rjrudin.marklogic.mgmt.alert.AlertConfigManager

class DeleteAllAlertConfigsTask extends MarkLogicTask {

    @TaskAction
    void deleteAlertConfigs() {
        new AlertConfigManager(getManageClient(), getAppConfig().getContentDatabaseName()).deleteAllConfigs()
    }
}
