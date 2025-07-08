/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.batch;

import com.marklogic.client.document.DocumentWriteOperation;

import java.util.List;

/**
 * Interface for writing batches of documents to pre-ML9 clusters (DMSDK can be used with ML9+).
 */
public interface BatchWriter {

	/**
	 * Give the writer a chance to perform any initialization it requires before it starts writing documents.
	 */
	void initialize();

	/**
	 * Write the given list of documents, as defined by the Java Client DocumentWriteOperation interface.
	 *
	 * @param items
	 */
	void write(List<? extends DocumentWriteOperation> items);

	/**
	 * Assuming that the writer is using a multi-threaded approach, call this to wait for the writer to finish
	 * performing all of its writes.
	 */
	void waitForCompletion();
}
