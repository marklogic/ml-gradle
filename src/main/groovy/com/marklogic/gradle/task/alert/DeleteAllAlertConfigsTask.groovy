package com.marklogic.gradle.task.alert

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.alert.AlertConfigManager

class DeleteAllAlertConfigsTask extends MarkLogicTask {

    @TaskAction
    void deleteAlertConfigs() {
        new AlertConfigManager(getManageClient(), getAppConfig().getContentDatabaseName()).deleteAllConfigs()
    }
}
