package com.marklogic.gradle.task.restapis

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class DeployRestApisTask extends MarkLogicTask {

	@TaskAction
	void deployRestApis() {
		deployWithCommandListProperty("mlRestApiCommands")
	}
}
