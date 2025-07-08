/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.datamovement

import com.marklogic.client.ext.datamovement.job.RemoveCollectionsJob
import org.gradle.api.tasks.TaskAction

class RemoveCollectionsTask extends DataMovementTask {

	@TaskAction
	void removeCollections() {
		runQueryBatcherJob(new RemoveCollectionsJob())
	}
}
