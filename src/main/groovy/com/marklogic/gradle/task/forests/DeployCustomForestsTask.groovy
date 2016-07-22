package com.marklogic.gradle.task.forests

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class DeployCustomForestsTask extends MarkLogicTask {

	@TaskAction
	void deployCustomForests() {
		invokeDeployerCommandWithClassName("DeployCustomForestsCommand")
	}
}
