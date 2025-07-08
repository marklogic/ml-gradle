/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.datamovement

import com.marklogic.client.ext.datamovement.job.ExportToZipJob
import org.gradle.api.tasks.TaskAction

class ExportToZipTask extends DataMovementTask {

	@TaskAction
	void exportToZip() {
		runQueryBatcherJob(new ExportToZipJob())
	}
}
