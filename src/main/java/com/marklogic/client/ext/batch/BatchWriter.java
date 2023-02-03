/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
