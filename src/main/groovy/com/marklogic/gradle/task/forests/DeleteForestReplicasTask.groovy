package com.marklogic.gradle.task.forests

import com.marklogic.appdeployer.command.forests.ConfigureForestReplicasCommand
import com.marklogic.gradle.task.AbstractConfirmableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * Task for invoking "undo" on an instance of ConfigureForestReplicasCommand. The command is exposed as a task
 * attribute so that its map of forest names and replica counts and be configured easily in a Gradle build file.
 */
class DeleteForestReplicasTask extends AbstractConfirmableTask {

	@Input
	@Optional
	ConfigureForestReplicasCommand command = new ConfigureForestReplicasCommand()

	@Override
	void executeIfConfirmed() {
		command.undo(getCommandContext())
	}
}
