/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt;

import com.marklogic.client.ext.helper.LoggingObject;

import java.util.function.Supplier;

/**
 * Immutable utility class for checking if a MarkLogic ManageClient connection is ready and functional.
 * This is particularly useful in CI/CD pipelines where MarkLogic may have just been installed
 * or restarted, and you need to wait until it's ready to handle management requests.
 * <p>
 * Uses ManageClient instead of DatabaseClient because ManageClient calls will continue to fail
 * until MarkLogic is truly ready, whereas DatabaseClient ping operations can succeed prematurely.
 */
public class ConnectionChecker extends LoggingObject {

	private final Supplier<ManageClient> manageClientSupplier;
	private final long waitInterval;  // milliseconds
	private final int maxAttempts;

	/**
	 * Create a new ConnectionChecker with the given ManageClient supplier and default settings.
	 * Default wait interval is 3000ms, default max attempts is 20.
	 *
	 * @param manageClientSupplier a Supplier that creates a new ManageClient for each attempt
	 */
	public ConnectionChecker(Supplier<ManageClient> manageClientSupplier) {
		this(manageClientSupplier, 3000L, 20);
	}

	/**
	 * Create a new ConnectionChecker with the given ManageClient supplier and custom settings.
	 *
	 * @param manageClientSupplier a Supplier that creates a new ManageClient for each attempt
	 * @param waitIntervalMs       wait interval in milliseconds (must be positive)
	 * @param maxAttempts          maximum number of attempts (must be positive)
	 */
	public ConnectionChecker(Supplier<ManageClient> manageClientSupplier, long waitIntervalMs, int maxAttempts) {
		if (manageClientSupplier == null) {
			throw new IllegalArgumentException("ManageClient supplier cannot be null");
		}
		if (waitIntervalMs <= 0) {
			throw new IllegalArgumentException("Wait interval must be positive");
		}
		if (maxAttempts <= 0) {
			throw new IllegalArgumentException("Max attempts must be positive");
		}
		this.manageClientSupplier = manageClientSupplier;
		this.waitInterval = waitIntervalMs;
		this.maxAttempts = maxAttempts;
	}

	/**
	 * Wait until the ManageClient connection is ready, retrying up to maxAttempts times.
	 * This checks the /manage/v2 endpoint, which will continue to fail until MarkLogic is truly ready.
	 *
	 * @throws RuntimeException if the connection is not ready after maxAttempts
	 */
	public void waitUntilReady() {
		logger.info("Waiting for MarkLogic to be ready (checking every {}ms, max attempts: {})", waitInterval, maxAttempts);

		for (int attempt = 1; attempt <= maxAttempts; attempt++) {
			String errorMessage;
			try {
				manageClientSupplier.get().getJson("/manage/v2");
				logger.info("MarkLogic is ready (connected on attempt {})", attempt);
				return;
			} catch (Exception e) {
				errorMessage = "Attempt %d failed: %s".formatted(attempt, e.getMessage());
			}

			if (attempt < maxAttempts) {
				logger.info("{}; waiting {}ms before retry...", errorMessage, waitInterval);
				try {
					Thread.sleep(waitInterval);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					throw new RuntimeException("Connection check was interrupted", e);
				}
			}
		}

		throw new RuntimeException(format("Failed to connect to MarkLogic after %d attempts (waited %dms between attempts)", maxAttempts, waitInterval));
	}

	public long getWaitInterval() {
		return waitInterval;
	}

	public int getMaxAttempts() {
		return maxAttempts;
	}
}
