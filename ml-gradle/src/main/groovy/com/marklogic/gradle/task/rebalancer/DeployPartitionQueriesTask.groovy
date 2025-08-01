/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.rebalancer

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class DeployPartitionQueriesTask extends MarkLogicTask {

	@TaskAction
	void deployPartitionQueries() {
		invokeDeployerCommandWithClassName("DeployPartitionQueriesCommand")
	}

}
