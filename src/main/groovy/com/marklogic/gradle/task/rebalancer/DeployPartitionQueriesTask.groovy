package com.marklogic.gradle.task.rebalancer

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class DeployPartitionQueriesTask extends MarkLogicTask {

	@TaskAction
	void deployPartitionQueries() {
		invokeDeployerCommandWithClassName("DeployPartitionQueriesCommand")
	}

}
