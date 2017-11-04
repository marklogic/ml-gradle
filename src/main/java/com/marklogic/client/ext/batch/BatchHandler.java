package com.marklogic.client.ext.batch;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentWriteOperation;

import java.util.List;

/**
 * Allows for customizing what RestBatchWriter does with each batch of documents.
 */
public interface BatchHandler {

	void handleBatch(DatabaseClient client, List<? extends DocumentWriteOperation> items);

}
