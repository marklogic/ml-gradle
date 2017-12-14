package com.marklogic.gradle.task.datamovement

import com.marklogic.client.datamovement.QueryBatchListener
import com.marklogic.client.ext.datamovement.QueryBatcherBuilder
import com.marklogic.client.ext.datamovement.listener.AddPermissionsListener
import org.gradle.api.tasks.TaskAction

class AddPermissionsTask extends DataMovementTask {

	@TaskAction
	void addPermissions() {
		if (!hasWhereSelectorProperty() || !project.hasProperty("permissions")) {
			println "Invalid input; task description: " + getDescription()
			return;
		}

		String[] permissions = getProject().property("permissions").split(",")
		QueryBatchListener listener = new AddPermissionsListener(permissions)

		BuilderAndMessage builderAndMessage = determineBuilderAndMessage()
		String message = "permissions " + Arrays.asList(permissions) + " to documents " + builderAndMessage.message

		println "Adding " + message
		applyWithQueryBatcherBuilder(listener, builderAndMessage.builder)
		println "Finished adding " + message
	}
}
