package com.marklogic.gradle.task.datamovement

import com.marklogic.client.datamovement.QueryBatchListener
import com.marklogic.client.ext.datamovement.CollectionsQueryBatcherBuilder
import com.marklogic.client.ext.datamovement.QueryBatcherBuilder
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
			println "Invalid inputs; task description: " + getDescription()
			return;
		}

		String[] collections = getProject().property("collections").split(",")

		BuilderAndMessage builderAndMessage = determineBuilderAndMessage()
		QueryBatcherBuilder builder
		String message = " from collections " + Arrays.asList(collections)

		if (builderAndMessage != null) {
			builder = builderAndMessage.builder
			message = builderAndMessage.message + message
		} else {
			// If no "where" clause was set, then assume the collections being removed are the ones to select from
			this.whereCollections = collections
			builder = new CollectionsQueryBatcherBuilder(this.whereCollections)
			message = "in collections " + Arrays.asList(this.whereCollections) + message
		}

		QueryBatchListener listener = new RemoveCollectionsListener(collections)

		println "Removing documents " + message
		applyWithQueryBatcherBuilder(listener, builder)
		println "Finished removing documents " + message
	}
}
