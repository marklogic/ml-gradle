package com.marklogic.gradle.task.datamovement

import com.marklogic.client.ext.datamovement.job.DeleteCollectionsJob
import org.gradle.api.tasks.TaskAction

class DeleteCollectionsTask extends DataMovementTask {

	String[] collections

	@TaskAction
	void deleteCollections() {
		project.ext.collections = (collections != null && collections.length > 0) ? collections.join(",") : null
		runQueryBatcherJob(new DeleteCollectionsJob())
	}
}
