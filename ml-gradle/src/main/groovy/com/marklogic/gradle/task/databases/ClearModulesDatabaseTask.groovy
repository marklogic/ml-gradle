/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.databases

import com.marklogic.appdeployer.command.Command
import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.resource.databases.DatabaseManager
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * Provides a "Command" property so that Data Hub can override how this task works without replacing it.
 */
class ClearModulesDatabaseTask extends MarkLogicTask {

	@Input
	@Optional
	Command command

	@TaskAction
	void clearModules() {
		if (command != null) {
			command.execute(getCommandContext())
		} else {
			new DatabaseManager(getManageClient()).clearDatabase(getAppConfig().getModulesDatabaseName());
		}
	}
}
