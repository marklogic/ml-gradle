package com.marklogic.gradle.task.datamovement

import com.marklogic.client.datamovement.QueryBatchListener
import com.marklogic.client.ext.datamovement.CollectionsQueryBatcherBuilder
import com.marklogic.client.ext.datamovement.QueryBatcherBuilder
import com.marklogic.client.ext.datamovement.UriPatternQueryBatcherBuilder
import com.marklogic.client.ext.datamovement.listener.AddPermissionsListener
import org.gradle.api.tasks.TaskAction

class AddPermissionsTask extends DataMovementTask {

	@TaskAction
	void addPermissions() {
		if ((!project.hasProperty("collections") && !project.hasProperty("uriPattern")) || !project.hasProperty("permissions")) {
			println "Invalid input; " + getDescription()
			return;
		}

		String[] permissions = getProject().property("permissions").split(",")
		QueryBatchListener listener = new AddPermissionsListener(permissions)
		QueryBatcherBuilder builder = null

		String message = "permissions " + Arrays.asList(permissions) + " to documents "
		if (project.hasProperty("collections")) {
			String[] collections = getProject().property("collections").split(",")
			message += "in collections " + Arrays.asList(collections)
			builder = new CollectionsQueryBatcherBuilder(collections)
		} else if (project.hasProperty("uriPattern")) {
			String pattern = getProject().property("uriPattern")
			message += "matching URI pattern " + pattern
			builder = new UriPatternQueryBatcherBuilder(pattern)
		}

		println "Adding " + message
		applyWithQueryBatcherBuilder(listener, builder)
		println "Finished adding " + message
	}
}
