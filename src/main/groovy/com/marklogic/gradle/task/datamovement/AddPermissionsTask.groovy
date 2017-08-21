package com.marklogic.gradle.task.datamovement

import com.marklogic.client.datamovement.QueryBatchListener
import com.marklogic.client.ext.datamovement.QueryBatcherBuilder
import com.marklogic.client.ext.datamovement.listener.AddPermissionsListener
import org.gradle.api.tasks.TaskAction

class AddPermissionsTask extends DataMovementTask {

	@TaskAction
	void addPermissions() {
		if ((!project.hasProperty("whereCollections") && !project.hasProperty("whereUriPattern")) || !project.hasProperty("permissions")) {
			println "Invalid input; task description: " + getDescription()
			return;
		}

		String[] permissions = getProject().property("permissions").split(",")
		QueryBatchListener listener = new AddPermissionsListener(permissions)
		QueryBatcherBuilder builder = null

		String message = "permissions " + Arrays.asList(permissions) + " to documents "

		if (hasWhereCollectionsProperty()) {
			builder = constructBuilderFromWhereCollections()
			message += "in collections " + Arrays.asList(this.whereCollections)
		} else if (hasWhereUriPatternProperty()) {
			builder = constructBuilderFromWhereUriPattern()
			message += "matching URI pattern " + this.whereUriPattern
		}

		println "Adding " + message
		applyWithQueryBatcherBuilder(listener, builder)
		println "Finished adding " + message
	}
}
