package com.marklogic.gradle.task.databases

import com.marklogic.appdeployer.command.Command
import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.resource.databases.DatabaseManager
import org.gradle.api.tasks.TaskAction

/**
 * Provides a "Command" property so that Data Hub can override how this task works without replacing it.
 */
class ClearModulesDatabaseTask extends MarkLogicTask {

	Command command

	@TaskAction
	void clearModules() {
		if (command != null) {
			command.execute(getCommandContext())
		} else {
			new DatabaseManager(context.getManageClient()).clearDatabase(getAppConfig().getModulesDatabaseName());
		}
	}
}
