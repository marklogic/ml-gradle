package com.marklogic.client.ext.modulesloader.impl;

import com.marklogic.client.ext.helper.LoggingObject;

public class SimpleLoadModulesFailureListener extends LoggingObject implements LoadModulesFailureListener {

	@Override
	public void processFailure(Throwable throwable) {
		if (logger.isErrorEnabled()) {
			logger.error("Error caught while loading modules, cause: " + throwable.getMessage(), throwable);
		}
	}
}
