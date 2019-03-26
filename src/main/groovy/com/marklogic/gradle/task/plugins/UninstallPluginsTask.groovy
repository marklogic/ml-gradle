package com.marklogic.gradle.task.plugins

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class UninstallPluginsTask extends MarkLogicTask {

	@TaskAction
	void uninstallPlugins() {
		undeployWithCommandListProperty("mlPluginCommands")
	}
}
