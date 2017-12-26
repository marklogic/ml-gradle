package com.marklogic.gradle.task.datamovement

import com.marklogic.client.ext.datamovement.job.SetPermissionsJob
import org.gradle.api.tasks.TaskAction

class SetPermissionsTask extends DataMovementTask {

	@TaskAction
	void setPermissions() {
		runQueryBatcherJob(new SetPermissionsJob())
	}
}
