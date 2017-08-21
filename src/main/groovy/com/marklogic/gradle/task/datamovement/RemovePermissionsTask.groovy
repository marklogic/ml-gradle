package com.marklogic.gradle.task.datamovement

import com.marklogic.client.datamovement.QueryBatchListener
import com.marklogic.client.ext.datamovement.CollectionsQueryBatcherBuilder
import com.marklogic.client.ext.datamovement.QueryBatcherBuilder
import com.marklogic.client.ext.datamovement.UriPatternQueryBatcherBuilder
import com.marklogic.client.ext.datamovement.listener.RemovePermissionsListener
import org.gradle.api.tasks.TaskAction

class RemovePermissionsTask extends DataMovementTask {

	@TaskAction
	void removePermissions() {
		if ((!project.hasProperty("whereCollections") && !project.hasProperty("whereUriPattern")) || !project.hasProperty("permissions")) {
			println "Invalid input; task description: " + getDescription()
			return;
		}

		String[] permissions = getProject().property("permissions").split(",")
		QueryBatchListener listener = new RemovePermissionsListener(permissions)
		QueryBatcherBuilder builder = null

		String message = "permissions " + Arrays.asList(permissions) + " from documents "

		if (hasWhereCollectionsProperty()) {
			builder = constructBuilderFromWhereCollections()
			message += "in collections " + Arrays.asList(this.whereCollections)
		} else if (hasWhereUriPatternProperty()) {
			builder = constructBuilderFromWhereUriPattern()
			message += "matching URI pattern " + this.whereUriPattern
		}

		println "Removing " + message
		applyWithQueryBatcherBuilder(listener, builder)
		println "Finished removing " + message
	}
}

