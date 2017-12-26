package com.marklogic.gradle.task.datamovement

import com.marklogic.client.ext.datamovement.job.AddPermissionsJob
import org.gradle.api.tasks.TaskAction

class AddPermissionsTask extends DataMovementTask {

	@TaskAction
	void addPermissions() {
		runQueryBatcherJob(new AddPermissionsJob())
	}
}
