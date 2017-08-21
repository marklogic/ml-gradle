package com.marklogic.gradle.task.datamovement

import com.marklogic.client.datamovement.QueryBatchListener
import com.marklogic.client.ext.datamovement.CollectionsQueryBatcherBuilder
import com.marklogic.client.ext.datamovement.QueryBatcherBuilder
import com.marklogic.client.ext.datamovement.UriPatternQueryBatcherBuilder
import com.marklogic.client.ext.datamovement.listener.SetPermissionsListener
import org.gradle.api.tasks.TaskAction

class SetPermissionsTask extends DataMovementTask {

	@TaskAction
	void setPermissions() {
		if ((!project.hasProperty("collections") && !project.hasProperty("uriPattern")) || !project.hasProperty("permissions")) {
			println "Invalid input; " + getDescription()
			return;
		}

		String[] permissions = getProject().property("permissions").split(",")
		QueryBatchListener listener = new SetPermissionsListener(permissions)
		QueryBatcherBuilder builder = null

		String message = "permissions " + Arrays.asList(permissions) + " on documents "

		if (hasWhereCollectionsProperty()) {
			builder = constructBuilderFromWhereCollections()
			message += "in collections " + Arrays.asList(this.whereCollections)
		} else if (hasWhereUriPatternProperty()) {
			builder = constructBuilderFromWhereUriPattern()
			message += "matching URI pattern " + this.whereUriPattern
		}

		println "Setting " + message
		applyWithQueryBatcherBuilder(listener, builder)
		println "Finished setting " + message
	}
}
