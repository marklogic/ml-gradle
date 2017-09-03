package com.marklogic.gradle.task.flexrep

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.resource.flexrep.ConfigManager

class DeleteAllFlexrepConfigsTask extends MarkLogicTask {

    @TaskAction
    void deleteFlexrepConfigs() {
        new ConfigManager(getManageClient(), getAppConfig().getContentDatabaseName()).deleteAllConfigs()
    }
}
