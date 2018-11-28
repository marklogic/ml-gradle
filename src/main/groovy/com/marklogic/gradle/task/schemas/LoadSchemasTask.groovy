package com.marklogic.gradle.task.schemas

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class LoadSchemasTask extends MarkLogicTask {

	@TaskAction
	void loadSchemas() {
		deployWithCommandListProperty("mlSchemaCommands")
	}
}
