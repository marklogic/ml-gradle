/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.configuration

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class DeployConfigurationsTask extends MarkLogicTask {

	@TaskAction
	void deployConfigurations() {
		deployWithCommandListProperty("mlConfigurationCommands")
	}
}
