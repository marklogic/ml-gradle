package com.marklogic.gradle.task.datamovement

import com.marklogic.client.datamovement.QueryBatchListener
import com.marklogic.client.ext.datamovement.CollectionsQueryBatcherBuilder
import com.marklogic.client.ext.datamovement.QueryBatcherBuilder
import com.marklogic.client.ext.datamovement.UriPatternQueryBatcherBuilder
import com.marklogic.client.ext.datamovement.listener.RemoveCollectionsListener
import org.gradle.api.tasks.TaskAction

class RemoveCollectionsTask extends DataMovementTask {

	/**
	 * This task allows for specifying source collections for documents that should be removed from target collections,
	 * but it's often the case that that list of collections is the same (and often just one collection). Thus, only the
	 * "collections" property needs to be specified if the lists are the same.
	 */
	@TaskAction
	void removeCollections() {
		if (!project.hasProperty("collections")) {
			println "Invalid inputs; " + getDescription()
			return;
		}

		String[] collections = getProject().property("collections").split(",")
		String message = " from collections " + Arrays.asList(collections)
		QueryBatchListener listener = new RemoveCollectionsListener(collections)
		QueryBatcherBuilder builder = null

		if (project.hasProperty("uriPattern")) {
			String pattern = project.property("uriPattern")
			message = "documents matching URI pattern " + pattern + message
			builder = new UriPatternQueryBatcherBuilder(pattern)
		}
		else {
			String[] sourceCollections = collections
			if (project.hasProperty("sourceCollections")) {
				sourceCollections = getProject().property("sourceCollections").split(",")
			}
			message = "documents in collections " + Arrays.asList(sourceCollections) + message
			builder = new CollectionsQueryBatcherBuilder(sourceCollections)
		}

		println "Removing " + message
		applyWithQueryBatcherBuilder(listener, builder)
		println "Finished removing " + message
	}
}
