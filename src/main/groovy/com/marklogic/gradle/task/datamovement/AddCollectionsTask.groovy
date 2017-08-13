package com.marklogic.gradle.task.datamovement

import com.marklogic.client.datamovement.QueryBatchListener
import com.marklogic.client.ext.datamovement.CollectionsQueryBatcherBuilder
import com.marklogic.client.ext.datamovement.QueryBatcherBuilder
import com.marklogic.client.ext.datamovement.UriPatternQueryBatcherBuilder
import com.marklogic.client.ext.datamovement.listener.AddCollectionsListener
import org.gradle.api.tasks.TaskAction

class AddCollectionsTask extends DataMovementTask {

	@TaskAction
	void addCollections() {
		if (
		(!project.hasProperty("sourceCollections") && !project.hasProperty("pattern")) ||
			!project.hasProperty("collections")
		) {
			println "Invalid inputs; " + getDescription()
			return;
		}

		String[] collections = getProject().property("collections").split(",")
		QueryBatchListener listener = new AddCollectionsListener(collections)
		QueryBatcherBuilder builder = null

		String message = " to collections " + Arrays.asList(collections);
		if (project.hasProperty("collections")) {
			String[] sourceCollections = getProject().property("sourceCollections").split(",")
			message = "documents in collections " + Arrays.asList(sourceCollections) + message
			builder = new CollectionsQueryBatcherBuilder(sourceCollections)
		} else if (project.hasProperty("uriPattern")) {
			String pattern = getProject().property("pattern")
			message = "documents matching URI pattern " + pattern + message
			builder = new UriPatternQueryBatcherBuilder(pattern)
		}

		println "Adding " + message
		applyWithQueryBatcherBuilder(listener, builder)
		println "Finished adding " + message
	}
}
