package com.marklogic.gradle.task.forests

import com.marklogic.appdeployer.command.forests.ConfigureForestReplicasCommand
import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * Task for executing an instance of ConfigureForestReplicasCommand. The command is exposed as a task attribute so
 * that its map of forest names and replica counts and be configured easily in a Gradle build file.
 */
class ConfigureForestReplicasTask extends MarkLogicTask {

	@Input
	@Optional
	ConfigureForestReplicasCommand command = new ConfigureForestReplicasCommand()

	@TaskAction
	void configureForestReplicas() {
		command.execute(getCommandContext())
	}
}
