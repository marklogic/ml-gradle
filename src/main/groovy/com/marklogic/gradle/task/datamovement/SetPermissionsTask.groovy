package com.marklogic.gradle.task.datamovement

import com.marklogic.client.datamovement.QueryBatchListener
import com.marklogic.client.ext.datamovement.listener.SetPermissionsListener
import org.gradle.api.tasks.TaskAction

class SetPermissionsTask extends DataMovementTask {

	@TaskAction
	void setPermissions() {
		if (!hasWhereSelectorProperty() || !project.hasProperty("permissions")) {
			println "Invalid input; task description: " + getDescription()
			return;
		}

		String[] permissions = getProject().property("permissions").split(",")
		QueryBatchListener listener = new SetPermissionsListener(permissions)

		BuilderAndMessage builderAndMessage = determineBuilderAndMessage()
		String message = "permissions " + Arrays.asList(permissions) + " on documents " + builderAndMessage.message

		println "Setting " + message
		applyWithQueryBatcherBuilder(listener, builderAndMessage.builder)
		println "Finished setting " + message
	}
}
