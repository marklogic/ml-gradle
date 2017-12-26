package com.marklogic.gradle.task.datamovement

import com.marklogic.client.ext.datamovement.job.RemovePermissionsJob
import org.gradle.api.tasks.TaskAction

class RemovePermissionsTask extends DataMovementTask {

	@TaskAction
	void removePermissions() {
		runQueryBatcherJob(new RemovePermissionsJob())
	}
}

