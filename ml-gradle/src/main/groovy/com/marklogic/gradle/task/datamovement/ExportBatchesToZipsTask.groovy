/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.datamovement

import com.marklogic.client.ext.datamovement.job.ExportBatchesToZipsJob
import org.gradle.api.tasks.TaskAction

class ExportBatchesToZipsTask extends DataMovementTask {

	@TaskAction
	void exportBatchesToZips() {
		runQueryBatcherJob(new ExportBatchesToZipsJob())
	}
}
