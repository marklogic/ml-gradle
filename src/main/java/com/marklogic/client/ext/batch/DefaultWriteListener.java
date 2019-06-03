package com.marklogic.client.ext.batch;

import com.marklogic.client.document.DocumentWriteOperation;

import java.util.List;

public class DefaultWriteListener extends WriteListenerSupport {

	private Throwable firstError;

	@Override
	public synchronized void onWriteFailure(Throwable ex, List<? extends DocumentWriteOperation> items) {
		if (firstError == null) {
			firstError = ex;
		}
	}

	@Override
	public void afterCompletion() {
		if (firstError != null) {
			throw new RuntimeException("Caught exception before writing was completed: " +
				firstError.getMessage(), firstError);
		}
	}
}
