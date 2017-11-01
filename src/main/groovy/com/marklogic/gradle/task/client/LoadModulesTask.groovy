package com.marklogic.gradle.task.client

import com.marklogic.appdeployer.command.modules.DeleteTestModulesCommand
import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.AppDeployer
import com.marklogic.appdeployer.command.modules.LoadModulesCommand
import com.marklogic.appdeployer.impl.SimpleAppDeployer
import com.marklogic.gradle.task.MarkLogicTask

class LoadModulesTask extends MarkLogicTask {

	@TaskAction
	void loadModules() {
		LoadModulesCommand command = null
		DeleteTestModulesCommand deleteCommand = null

		// Check for a LoadModulesCommand in the AppDeployer first, as that may have additional configuration that we
		// don't want to have to duplicate on this task
		AppDeployer d = getAppDeployer()
		if (d instanceof SimpleAppDeployer) {
			command = d.getCommand("LoadModulesCommand")
			deleteCommand = d.getCommand("DeleteTestModulesCommand")
		}

		if (command == null) {
			command = new LoadModulesCommand()
		}
		if (deleteCommand == null) {
			deleteCommand = new DeleteTestModulesCommand()
		}

		command.execute(getCommandContext())
		deleteCommand.execute(getCommandContext())
	}
}
