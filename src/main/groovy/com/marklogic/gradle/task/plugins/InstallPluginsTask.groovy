package com.marklogic.gradle.task.plugins

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class InstallPluginsTask extends MarkLogicTask {

	@TaskAction
	void installPlugins() {
		deployWithCommandListProperty("mlPluginCommands")
	}

}
