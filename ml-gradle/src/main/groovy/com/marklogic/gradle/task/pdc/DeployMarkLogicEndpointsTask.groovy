/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.pdc

import com.marklogic.appdeployer.command.pdc.DeployMarkLogicEndpointsCommand
import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class DeployMarkLogicEndpointsTask extends MarkLogicTask {

	@TaskAction
	void deployMarkLogicEndpoints() {
		invokeDeployerCommandWithClassName(DeployMarkLogicEndpointsCommand.class.getSimpleName())
	}
}
