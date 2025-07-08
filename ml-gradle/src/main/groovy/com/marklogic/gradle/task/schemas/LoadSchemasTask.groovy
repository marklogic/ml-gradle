/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.schemas

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class LoadSchemasTask extends MarkLogicTask {

	@TaskAction
	void loadSchemas() {
		deployWithCommandListProperty("mlSchemaCommands")
	}
}
