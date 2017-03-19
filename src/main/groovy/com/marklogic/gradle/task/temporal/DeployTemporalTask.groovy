package com.marklogic.gradle.task.temporal

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask

class DeployTemporalTask extends MarkLogicTask {

	@TaskAction
	void deployCpf() {
		deployWithCommandListProperty("mlTemporalCommands")
	}
}
