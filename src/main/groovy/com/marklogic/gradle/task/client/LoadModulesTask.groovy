package com.marklogic.gradle.task.client

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class LoadModulesTask extends MarkLogicTask {

	@TaskAction
	void loadModules() {
		deployWithCommandListProperty("mlModuleCommands")
	}
}
