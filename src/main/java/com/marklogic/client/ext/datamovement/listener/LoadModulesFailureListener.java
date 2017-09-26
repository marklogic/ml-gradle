package com.marklogic.client.ext.datamovement.listener;

public interface LoadModulesFailureListener {
	void processFailure(Throwable throwable);
}
