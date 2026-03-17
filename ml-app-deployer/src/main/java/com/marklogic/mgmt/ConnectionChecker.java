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
	private final int minSuccessfulAttempts;

	/**
	 * Create a new ConnectionChecker with the given ManageClient supplier and default settings.
	 * Default wait interval is 3000ms, default max attempts is 20, default min successful attempts is 2.
	 *
	 * @param manageClientSupplier a Supplier that creates a new ManageClient for each attempt
	 */
	public ConnectionChecker(Supplier<ManageClient> manageClientSupplier) {
		this(manageClientSupplier, 3000L, 20, 2);
	}

	/**
	 * Create a new ConnectionChecker with the given ManageClient supplier and custom settings.
	 *
	 * @param manageClientSupplier  a Supplier that creates a new ManageClient for each attempt
	 * @param waitIntervalMs        wait interval in milliseconds (must be positive)
	 * @param maxAttempts           maximum number of attempts (must be positive)
	 * @param minSuccessfulAttempts minimum number of consecutive successful attempts required (must be positive)
	 */
	public ConnectionChecker(Supplier<ManageClient> manageClientSupplier, long waitIntervalMs, int maxAttempts, int minSuccessfulAttempts) {
		if (manageClientSupplier == null) {
			throw new IllegalArgumentException("ManageClient supplier cannot be null");
		}
		if (waitIntervalMs <= 0) {
			throw new IllegalArgumentException("Wait interval must be positive");
		}
		if (maxAttempts <= 0) {
			throw new IllegalArgumentException("Max attempts must be positive");
		}
		if (minSuccessfulAttempts <= 0) {
			throw new IllegalArgumentException("Min successful attempts must be positive");
		}
		this.manageClientSupplier = manageClientSupplier;
		this.waitInterval = waitIntervalMs;
		this.maxAttempts = maxAttempts;
		this.minSuccessfulAttempts = minSuccessfulAttempts;
	}

	/**
	 * Wait until the ManageClient connection is ready, requiring a minimum number of consecutive successful attempts.
	 * This checks the /manage/v2 endpoint, which will continue to fail until MarkLogic is truly ready.
	 * Requiring multiple consecutive successes helps ensure MarkLogic is fully initialized and stable.
	 *
	 * @throws RuntimeException if the connection is not ready after maxAttempts
	 */
	public void waitUntilReady() {
		logger.info("Waiting for MarkLogic to be ready (checking every {}ms, max attempts: {}, min successful attempts: {})",
			waitInterval, maxAttempts, minSuccessfulAttempts);

		int consecutiveSuccesses = 0;
		for (int attempt = 1; attempt <= maxAttempts; attempt++) {
			try {
				manageClientSupplier.get().getJson("/manage/v2");
				consecutiveSuccesses++;
				logger.info("Connection successful (attempt {}, consecutive successes: {})", attempt, consecutiveSuccesses);

				if (consecutiveSuccesses >= minSuccessfulAttempts) {
					logger.info("MarkLogic is ready ({} consecutive successful connections)", consecutiveSuccesses);
					return;
				}

				if (attempt < maxAttempts) {
					logger.info("Waiting {}ms before next verification...", waitInterval);
					sleep();
				}
			} catch (Exception e) {
				consecutiveSuccesses = 0;
				handleConnectionFailure(attempt, e);
			}
		}

		throw new RuntimeException("Failed to connect to MarkLogic after %d attempts (waited %dms between attempts)".formatted(maxAttempts, waitInterval));
	}

	private void handleConnectionFailure(int attempt, Exception e) {
		String errorMessage = "Attempt %d failed: %s".formatted(attempt, e.getMessage());

		if (attempt < maxAttempts) {
			logger.info("{}; waiting {}ms before retry...", errorMessage, waitInterval);
			sleep();
		}
	}

	private void sleep() {
		try {
			Thread.sleep(waitInterval);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Connection check was interrupted", ie);
		}
	}

	public long getWaitInterval() {
		return waitInterval;
	}

	public int getMaxAttempts() {
		return maxAttempts;
	}

	public int getMinSuccessfulAttempts() {
		return minSuccessfulAttempts;
	}
}
