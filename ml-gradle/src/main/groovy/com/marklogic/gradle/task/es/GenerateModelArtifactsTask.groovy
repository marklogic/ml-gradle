/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.es

import com.marklogic.appdeployer.command.es.GenerateModelArtifactsCommand
import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class GenerateModelArtifactsTask extends MarkLogicTask {

	@TaskAction
	void generateModelArtifacts() {
		new GenerateModelArtifactsCommand().execute(getCommandContext())
	}
}
