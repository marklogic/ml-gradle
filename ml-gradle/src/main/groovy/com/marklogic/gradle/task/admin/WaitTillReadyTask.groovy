/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task.admin


import com.marklogic.client.ext.util.ConnectionChecker
import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * Waits until MarkLogic is ready by using ConnectionChecker to verify database connectivity via the App-Services port (8000).
 * This is useful in CI/CD pipelines where MarkLogic may have just been installed or restarted.
 * Delegates to ConnectionChecker for the actual connection testing logic.
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
			{ -> getAppConfig().newAppServicesDatabaseClient(null) } as java.util.function.Supplier,
			waitInterval,
			maxAttempts
		).waitUntilReady()
	}
}
