package com.marklogic.gradle.task.datamovement

import com.marklogic.client.ext.datamovement.job.ExportToFileJob
import org.gradle.api.tasks.TaskAction

class ExportToFileTask extends DataMovementTask {

	@TaskAction
	void exportToFile() {
		runQueryBatcherJob(new ExportToFileJob())
	}
}
