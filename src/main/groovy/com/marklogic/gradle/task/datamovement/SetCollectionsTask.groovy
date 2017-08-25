package com.marklogic.gradle.task.datamovement

import com.marklogic.client.datamovement.QueryBatchListener
import com.marklogic.client.ext.datamovement.QueryBatcherBuilder
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
		QueryBatcherBuilder builder = null

		String message = " collections " + Arrays.asList(collections) + " on documents ";

		if (hasWhereCollectionsProperty()) {
			builder = constructBuilderFromWhereCollections()
			message += "in collections " + Arrays.asList(this.whereCollections)
		} else if (hasWhereUriPatternProperty()) {
			builder = constructBuilderFromWhereUriPattern()
			message += "matching URI pattern " + this.whereUriPattern
		} else if (hasWhereUrisQueryProperty()) {
			builder = constructBuilderFromWhereUrisQuery()
			message += "matching URIs query " + this.whereUrisQuery
		}

		println "Setting" + message
		applyWithQueryBatcherBuilder(listener, builder)
		println "Finished setting" + message
	}
}
