/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.admin

import com.marklogic.gradle.task.MarkLogicTask
import com.marklogic.mgmt.ConnectionChecker
import com.marklogic.mgmt.ManageClient
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * Waits until MarkLogic is ready by using ConnectionChecker to verify management API connectivity.
 * This is useful in CI/CD pipelines where MarkLogic may have just been installed or restarted.
 * Uses ManageClient instead of DatabaseClient because ManageClient calls will continue to fail
 * until MarkLogic is truly ready, whereas DatabaseClient ping operations can succeed prematurely.
 */
class WaitTillReadyTask extends MarkLogicTask {

	@Input
	@Optional
	Long waitInterval = 3000L

	@Input
	@Optional
	Integer maxAttempts = 20

	@TaskAction
	void waitTillReady() {
		new ConnectionChecker(
			{ -> new ManageClient(getManageConfig()) } as java.util.function.Supplier,
			waitInterval,
			maxAttempts
		).waitUntilReady()
	}
}
