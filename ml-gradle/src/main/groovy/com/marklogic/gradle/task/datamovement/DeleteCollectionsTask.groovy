/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.datamovement

import com.marklogic.client.ext.datamovement.job.DeleteCollectionsJob
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

class DeleteCollectionsTask extends DataMovementTask {

	@Input
	@Optional
	String[] collections

	@TaskAction
	void deleteCollections() {
		project.ext.collections = (collections != null && collections.length > 0) ? collections.join(",") : project.ext.collections
		runQueryBatcherJob(new DeleteCollectionsJob())
	}
}
