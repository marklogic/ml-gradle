package com.marklogic.gradle.task.datamovement

import com.marklogic.client.ext.datamovement.job.SetCollectionsJob
import org.gradle.api.tasks.TaskAction

class SetCollectionsTask extends DataMovementTask {

	@TaskAction
	void setCollections() {
		runQueryBatcherJob(new SetCollectionsJob())
	}
}
