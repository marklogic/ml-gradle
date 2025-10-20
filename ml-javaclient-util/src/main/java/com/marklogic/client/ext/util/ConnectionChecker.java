/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.util;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.helper.LoggingObject;

import java.util.function.Supplier;

/**
 * Immutable utility class for checking if a MarkLogic DatabaseClient connection is ready and functional.
 * This is particularly useful in CI/CD pipelines where MarkLogic may have just been installed
 * or restarted, and you need to wait until it's ready to handle database requests.
 */

public class ConnectionChecker extends LoggingObject {

	private final Supplier<DatabaseClient> clientSupplier;
	private final long waitInterval;  // milliseconds
	private final int maxAttempts;

	/**
	 * Create a new ConnectionChecker with the given DatabaseClient supplier and default settings.
	 * Default wait interval is 3000ms, default max attempts is 20.
	 *
	 * @param clientSupplier a Supplier that creates a new DatabaseClient for each attempt
	 */
	public ConnectionChecker(Supplier<DatabaseClient> clientSupplier) {
		this(clientSupplier, 3000L, 20);
	}

	/**
	 * Create a new ConnectionChecker with the given DatabaseClient supplier and custom settings.
	 *
	 * @param clientSupplier a Supplier that creates a new DatabaseClient for each attempt
	 * @param waitIntervalMs wait interval in milliseconds (must be positive)
	 * @param maxAttempts    maximum number of attempts (must be positive)
	 */
	public ConnectionChecker(Supplier<DatabaseClient> clientSupplier, long waitIntervalMs, int maxAttempts) {
		if (clientSupplier == null) {
			throw new IllegalArgumentException("DatabaseClient supplier cannot be null");
		}
		if (waitIntervalMs <= 0) {
			throw new IllegalArgumentException("Wait interval must be positive");
		}
		if (maxAttempts <= 0) {
			throw new IllegalArgumentException("Max attempts must be positive");
		}
		this.clientSupplier = clientSupplier;
		this.waitInterval = waitIntervalMs;
		this.maxAttempts = maxAttempts;
	}

	/**
	 * Wait until the DatabaseClient connection is ready, retrying up to maxAttempts times.
	 *
	 * @return true when the connection becomes ready
	 * @throws RuntimeException if the connection is not ready after maxAttempts
	 */
	public boolean waitUntilReady() {
		logger.info("Waiting for MarkLogic to be ready (checking every {}ms, max attempts: {})", waitInterval, maxAttempts);

		for (int attempt = 1; attempt <= maxAttempts; attempt++) {
			String errorMessage;
			try (DatabaseClient client = clientSupplier.get()) {
				DatabaseClient.ConnectionResult result = client.checkConnection();
				if (result.isConnected()) {
					logger.info("MarkLogic is ready (connected on attempt {})", attempt);
					return true;
				}
				errorMessage = "Attempt %d failed with status code %d: %s".formatted(attempt, result.getStatusCode(), result.getErrorMessage());
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
