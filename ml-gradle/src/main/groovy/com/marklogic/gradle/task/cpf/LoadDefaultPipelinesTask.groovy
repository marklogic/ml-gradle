/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.cpf

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.resource.cpf.PipelineManager
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

class LoadDefaultPipelinesTask extends MarkLogicTask {

	@Input
	@Optional
    String databaseName

    @TaskAction
    void loadDefaultPipelines() {
        def dbName = databaseName != null ? databaseName : getAppConfig().getTriggersDatabaseName()
        new PipelineManager(getManageClient(), dbName).loadDefaultPipelines()
    }
}
