/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.batch;

import com.marklogic.client.document.DocumentWriteOperation;

import java.util.List;

/**
 * Callback interface for when a list of DocumentWriteOperation instances cannot be written to MarkLogic.
 */
public interface WriteListener {

	void onWriteFailure(Throwable ex, List<? extends DocumentWriteOperation> items);

	void afterCompletion();
}
