package com.marklogic.client.ext.modulesloader.impl;

import com.marklogic.client.ext.helper.LoggingObject;

import java.util.function.Supplier;

public class SimpleLoadModulesFailureListener extends LoggingObject implements LoadModulesFailureListener, Supplier<Throwable> {

	private Throwable firstThrowable;

	@Override
	public void processFailure(Throwable throwable) {
		if (firstThrowable == null) {
			firstThrowable = throwable;
		}
		if (logger.isErrorEnabled()) {
			logger.error("Error caught while loading modules, cause: " + throwable.getMessage(), throwable);
		}
	}

	@Override
	public Throwable get() {
		return firstThrowable;
	}
}
