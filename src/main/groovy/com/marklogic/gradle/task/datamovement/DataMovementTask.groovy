package com.marklogic.gradle.task.datamovement

import com.marklogic.client.DatabaseClient
import com.marklogic.client.datamovement.QueryBatchListener
import com.marklogic.client.ext.datamovement.QueryBatcherBuilder
import com.marklogic.client.ext.datamovement.QueryBatcherTemplate
import com.marklogic.gradle.task.MarkLogicTask

class DataMovementTask extends MarkLogicTask {

	void applyOnCollections(QueryBatchListener listener, String... collections) {
		DatabaseClient client = newClient()
		try {
			new QueryBatcherTemplate(client).applyOnCollections(listener, collections);
		} finally {
			client.release()
		}
	}

	void applyOnUriPattern(QueryBatchListener listener, String uriPattern) {
		DatabaseClient client = newClient()
		try {
			new QueryBatcherTemplate(client).applyOnUriPattern(listener, uriPattern);
		} finally {
			client.release()
		}
	}

	void applyWithQueryBatcherBuilder(QueryBatchListener listener, QueryBatcherBuilder builder) {
		DatabaseClient client = newClient()
		try {
			new QueryBatcherTemplate(client).apply(listener, builder)
		} finally {
			client.release()
		}
	}
}
