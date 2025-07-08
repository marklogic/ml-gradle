/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.batch;

import com.marklogic.client.document.DocumentWriteOperation;

import java.util.List;

/**
 * WriteListener implementors should extend this to avoid issues when methods are added to
 * WriteListener.
 */
public class WriteListenerSupport implements WriteListener {

	@Override
	public void onWriteFailure(Throwable ex, List<? extends DocumentWriteOperation> items) {
	}

	@Override
	public void afterCompletion() {
	}
}
