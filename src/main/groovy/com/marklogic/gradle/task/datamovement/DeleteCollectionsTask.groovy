package com.marklogic.gradle.task.datamovement

import com.marklogic.client.datamovement.DeleteListener
import org.gradle.api.tasks.TaskAction

class DeleteCollectionsTask extends DataMovementTask {

	String[] collections

	@TaskAction
	void deleteCollections() {
		if (collections == null || collections.length == 0) {
			if (!project.hasProperty("collections")) {
				println "Invalid inputs; task description: " + getDescription()
				return
			}
			collections = getProject().property("collections").split(",")
		}

		String message = "collections " + Arrays.asList(collections)
		println "Deleting " + message
		applyOnCollections(new DeleteListener(), collections)
		println "Finished deleting " + message
	}
}
