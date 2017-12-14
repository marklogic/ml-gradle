package com.marklogic.gradle.task.datamovement

import com.marklogic.client.datamovement.QueryBatchListener
import com.marklogic.client.ext.datamovement.listener.SetCollectionsListener
import org.gradle.api.tasks.TaskAction

class SetCollectionsTask extends DataMovementTask {

	@TaskAction
	void setCollections() {
		if (!hasWhereSelectorProperty() || !project.hasProperty("collections")) {
			println "Invalid inputs; task description: " + getDescription()
			return;
		}

		String[] collections = getProject().property("collections").split(",")
		QueryBatchListener listener = new SetCollectionsListener(collections)

		BuilderAndMessage builderAndMessage = determineBuilderAndMessage()
		String message = " collections " + Arrays.asList(collections) + " on documents " + builderAndMessage.message

		println "Setting" + message
		applyWithQueryBatcherBuilder(listener, builderAndMessage.builder)
		println "Finished setting" + message
	}
}
