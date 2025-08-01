/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.datamovement

import com.marklogic.client.ext.datamovement.job.AddCollectionsJob
import org.gradle.api.tasks.TaskAction

class AddCollectionsTask extends DataMovementTask {

	@TaskAction
	void addCollections() {
		runQueryBatcherJob(new AddCollectionsJob())
	}
}
