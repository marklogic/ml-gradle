package com.marklogic.gradle.task.datamovement

import com.marklogic.client.datamovement.QueryBatchListener
import com.marklogic.client.ext.datamovement.QueryBatcherBuilder
import com.marklogic.client.ext.datamovement.listener.SetCollectionsListener
import org.gradle.api.tasks.TaskAction

class SetCollectionsTask extends DataMovementTask {

	@TaskAction
	void setCollections() {
		if (
		(!project.hasProperty("whereCollections") && !project.hasProperty("pattern")) ||
			!project.hasProperty("collections")
		) {
			println "Invalid inputs; task description: " + getDescription()
			return;
		}

		String[] collections = getProject().property("collections").split(",")
		QueryBatchListener listener = new SetCollectionsListener(collections)
		QueryBatcherBuilder builder = null

		String message = " collections " + Arrays.asList(collections);

		if (hasWhereCollectionsProperty()) {
			builder = constructBuilderFromWhereCollections()
			message += " on documents in collections " + Arrays.asList(this.whereCollections)
		} else if (hasWhereUriPatternProperty()) {
			builder = constructBuilderFromWhereUriPattern()
			message += " on documents matching URI pattern " + this.whereUriPattern
		}

		println "Setting " + message
		applyWithQueryBatcherBuilder(listener, builder)
		println "Finished setting " + message
	}
}
