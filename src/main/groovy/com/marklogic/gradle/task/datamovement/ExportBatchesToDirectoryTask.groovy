package com.marklogic.gradle.task.datamovement

import com.marklogic.client.ext.datamovement.job.ExportBatchesToDirectoryJob
import org.gradle.api.tasks.TaskAction

class ExportBatchesToDirectoryTask extends DataMovementTask {

	@TaskAction
	void exportBatchesToDirectory() {
		runQueryBatcherJob(new ExportBatchesToDirectoryJob())
	}
}
