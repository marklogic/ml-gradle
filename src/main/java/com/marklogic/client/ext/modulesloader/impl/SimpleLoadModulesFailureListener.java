package com.marklogic.client.ext.modulesloader.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.helper.LoggingObject;

import java.util.function.Supplier;

public class SimpleLoadModulesFailureListener extends LoggingObject implements LoadModulesFailureListener, Supplier<Throwable> {

	private Throwable firstThrowable;

	@Override
	public void processFailure(Throwable throwable, DatabaseClient databaseClient) {
		final String message = format("Error occurred while loading modules; host: %s; port: %d; cause: %s", databaseClient.getHost(), databaseClient.getPort(), throwable.getMessage());
		final RuntimeException exception = new RuntimeException(message, throwable);
		if (firstThrowable == null) {
			firstThrowable = exception;
		}
		if (logger.isErrorEnabled()) {
			logger.error(message, throwable);
		}
	}

	@Override
	public Throwable get() {
		return firstThrowable;
	}
}
