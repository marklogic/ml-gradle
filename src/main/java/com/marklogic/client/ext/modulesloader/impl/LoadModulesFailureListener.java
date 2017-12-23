package com.marklogic.client.ext.modulesloader.impl;

public interface LoadModulesFailureListener {
	void processFailure(Throwable throwable);
}
