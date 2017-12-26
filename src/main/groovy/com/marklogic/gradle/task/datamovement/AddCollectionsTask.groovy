package com.marklogic.gradle.task.datamovement

import com.marklogic.client.ext.datamovement.job.AddCollectionsJob
import org.gradle.api.tasks.TaskAction

class AddCollectionsTask extends DataMovementTask {

	@TaskAction
	void addCollections() {
		runQueryBatcherJob(new AddCollectionsJob())
	}
}
