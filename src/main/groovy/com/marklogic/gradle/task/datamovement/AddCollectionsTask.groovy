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
		QueryBatcherBuilder builder = null

		String message = " to collections " + Arrays.asList(collections);

		if (hasWhereCollectionsProperty()) {
			builder = constructBuilderFromWhereCollections()
			message = "in collections " + Arrays.asList(this.whereCollections) + message
		} else if (hasWhereUriPatternProperty()) {
			builder = constructBuilderFromWhereUriPattern()
			message = "matching URI pattern " + this.whereUriPattern + message
		} else if (hasWhereUrisQueryProperty()) {
			builder = constructBuilderFromWhereUrisQuery()
			message = "matching URIs query " + this.whereUrisQuery + message
		}

		println "Adding documents " + message
		applyWithQueryBatcherBuilder(listener, builder)
		println "Finished adding documents " + message
	}
}
