package com.marklogic.gradle.task.datamovement

import com.marklogic.client.datamovement.QueryBatchListener
import com.marklogic.client.ext.datamovement.CollectionsQueryBatcherBuilder
import com.marklogic.client.ext.datamovement.QueryBatcherBuilder
import com.marklogic.client.ext.datamovement.UriPatternQueryBatcherBuilder
import com.marklogic.client.ext.datamovement.listener.SetCollectionsListener
import org.gradle.api.tasks.TaskAction

class SetCollectionsTask extends DataMovementTask {

	@TaskAction
	void setCollections() {
		if (
		(!project.hasProperty("sourceCollections") && !project.hasProperty("pattern")) ||
			!project.hasProperty("collections")
		) {
			println "Invalid inputs; " + getDescription()
			return;
		}

		String[] collections = getProject().property("collections").split(",")
		QueryBatchListener listener = new SetCollectionsListener(collections)
		QueryBatcherBuilder builder = null

		String message = " collections " + Arrays.asList(collections);
		if (project.hasProperty("collections")) {
			String[] sourceCollections = getProject().property("sourceCollections").split(",")
			message += " on documents in collections " + Arrays.asList(sourceCollections)
			builder = new CollectionsQueryBatcherBuilder(sourceCollections)
		} else if (project.hasProperty("uriPattern")) {
			String pattern = getProject().property("pattern")
			message += " on documents matching URI pattern " + pattern
			builder = new UriPatternQueryBatcherBuilder(pattern)
		}

		println "Setting " + message
		applyWithQueryBatcherBuilder(listener, builder)
		println "Finished setting " + message
	}
}
