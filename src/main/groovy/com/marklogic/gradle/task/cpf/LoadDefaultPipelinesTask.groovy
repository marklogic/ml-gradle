package com.marklogic.gradle.task.cpf

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.resource.cpf.PipelineManager

class LoadDefaultPipelinesTask extends MarkLogicTask {

    String databaseName

    @TaskAction
    void loadDefaultPipelines() {
        def dbName = databaseName != null ? databaseName : getAppConfig().getTriggersDatabaseName()
        new PipelineManager(getManageClient()).loadDefaultPipelines(dbName)
    }
}
