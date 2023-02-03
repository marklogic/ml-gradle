/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
