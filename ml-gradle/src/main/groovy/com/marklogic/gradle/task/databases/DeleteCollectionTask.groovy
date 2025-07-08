/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.databases

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

class DeleteCollectionTask extends MarkLogicTask {

	@Input
	@Optional
	String collection

	@Input
	boolean showEstimate = true

	@TaskAction
	void deleteCollection() {
		String coll = collection
		if (project.hasProperty("collection")) {
			coll = project.property("collection")
		}
		if (coll == null) {
			println "Please specify a collection to delete; e.g. -Pcollection=changeme"
			return
		}

		def client = newClient()
		try {
			if (showEstimate) {
				try {
					def estimate = client.newServerEval().xquery("xdmp:estimate(collection('" + coll + "'))").eval().next().getAs(String.class)
					println "Collection " + coll + " has " + estimate + " documents in it"
				} catch (Exception e) {
					// Don't let this bomb the task
					println "Unable to get estimate of collection size, cause: " + e.getMessage()
				}
			}
			println "Deleting collection " + coll + " in the content database; note that this may timeout if your collection has a sufficiently large enough number of documents"
			client.newServerEval().xquery("xdmp:collection-delete('" + coll + "')").eval()
		} finally {
			client.release()
		}
		println "Finished deleting collection " + coll
	}
}
