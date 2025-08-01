/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.cluster

import com.marklogic.appdeployer.command.clusters.ModifyLocalClusterCommand
import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class ModifyClusterTask extends MarkLogicTask {

	@TaskAction
	void modifyCluster() {
		invokeDeployerCommandWithClassName(ModifyLocalClusterCommand.class.getSimpleName())
	}
}
