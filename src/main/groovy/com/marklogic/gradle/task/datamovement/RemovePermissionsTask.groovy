package com.marklogic.gradle.task.datamovement

import com.marklogic.client.datamovement.QueryBatchListener
import com.marklogic.client.ext.datamovement.listener.RemovePermissionsListener
import org.gradle.api.tasks.TaskAction

class RemovePermissionsTask extends DataMovementTask {

	@TaskAction
	void removePermissions() {
		if (!hasWhereSelectorProperty() || !project.hasProperty("permissions")) {
			println "Invalid input; task description: " + getDescription()
			return;
		}

		String[] permissions = getProject().property("permissions").split(",")
		QueryBatchListener listener = new RemovePermissionsListener(permissions)

		BuilderAndMessage builderAndMessage = determineBuilderAndMessage()
		String message = "permissions " + Arrays.asList(permissions) + " from documents " + builderAndMessage.message

		println "Removing " + message
		applyWithQueryBatcherBuilder(listener, builderAndMessage.builder)
		println "Finished removing " + message
	}
}

