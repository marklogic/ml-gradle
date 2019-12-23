package com.marklogic.gradle.task.rebalancer

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class DeployPartitionsTask extends MarkLogicTask {

	@TaskAction
	void deployPartitions() {
		invokeDeployerCommandWithClassName("DeployPartitionsCommand")
	}
}
