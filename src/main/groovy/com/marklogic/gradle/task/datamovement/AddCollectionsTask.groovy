package com.marklogic.gradle.task.datamovement

import com.marklogic.client.datamovement.QueryBatchListener
import com.marklogic.client.ext.datamovement.QueryBatcherBuilder
import com.marklogic.client.ext.datamovement.listener.AddCollectionsListener
import org.gradle.api.tasks.TaskAction

class AddCollectionsTask extends DataMovementTask {

	@TaskAction
	void addCollections() {
		if (!hasWhereSelectorProperty() || !project.hasProperty("collections")) {
			println "Invalid inputs; task description: " + getDescription()
			return;
		}

		String[] collections = getProject().property("collections").split(",")
		QueryBatchListener listener = new AddCollectionsListener(collections)

		BuilderAndMessage builderAndMessage = determineBuilderAndMessage()
		String message = builderAndMessage.message + " to collections " + Arrays.asList(collections);

		println "Adding documents " + message
		applyWithQueryBatcherBuilder(listener, builderAndMessage.builder)
		println "Finished adding documents " + message
	}
}
