package com.marklogic.gradle.task.datamovement

import com.marklogic.client.DatabaseClient
import com.marklogic.client.datamovement.QueryBatch
import com.marklogic.client.datamovement.QueryBatchListener
import com.marklogic.client.ext.datamovement.CollectionsQueryBatcherBuilder
import com.marklogic.client.ext.datamovement.QueryBatcherBuilder
import com.marklogic.client.ext.datamovement.QueryBatcherTemplate
import com.marklogic.client.ext.datamovement.UriPatternQueryBatcherBuilder
import com.marklogic.gradle.task.MarkLogicTask

class DataMovementTask extends MarkLogicTask {

	String whereUriPattern
	String[] whereCollections

	boolean hasWhereCollectionsProperty() {
		return project.hasProperty("whereCollections")
	}

	boolean hasWhereUriPatternProperty() {
		return project.hasProperty("whereUriPattern")
	}

	QueryBatcherBuilder constructBuilderFromWhereCollections() {
		this.whereCollections = getProject().property("whereCollections").split(",")
		return new CollectionsQueryBatcherBuilder(this.whereCollections)
	}

	QueryBatcherBuilder constructBuilderFromWhereUriPattern() {
		this.whereUriPattern = getProject().property("whereUriPattern")
		return new UriPatternQueryBatcherBuilder(this.whereUriPattern)
	}

	void applyOnCollections(QueryBatchListener listener, String... collections) {
		DatabaseClient client = newClient()
		try {
			newQueryBatcherTemplate(client).applyOnCollections(listener, collections);
		} finally {
			client.release()
		}
	}

	void applyOnUriPattern(QueryBatchListener listener, String uriPattern) {
		DatabaseClient client = newClient()
		try {
			newQueryBatcherTemplate(client).applyOnUriPattern(listener, uriPattern);
		} finally {
			client.release()
		}
	}

	void applyWithQueryBatcherBuilder(QueryBatchListener listener, QueryBatcherBuilder builder) {
		DatabaseClient client = newClient()
		try {
			newQueryBatcherTemplate(client).apply(listener, builder)
		} finally {
			client.release()
		}
	}

	/**
	 * Supports the following properties:
	 * - threadCount
	 * - batchSize
	 * - applyConsistentSnapshot
	 * - awaitCompletion
	 * - stopJob
	 * - jobName
	 *
	 * Can override this method in a subclass to further configure the QueryBatcherTemplate that's returned.
	 *
	 * @param client
	 * @return
	 */
	QueryBatcherTemplate newQueryBatcherTemplate(DatabaseClient client) {
		QueryBatcherTemplate t = new QueryBatcherTemplate(client)
		if (project.hasProperty("threadCount")) {
			t.setThreadCount(Integer.parseInt(project.property("threadCount")))
		}
		if (project.hasProperty("batchSize")) {
			t.setBatchSize(Integer.parseInt(project.property("batchSize")))
		}
		if (project.hasProperty("applyConsistentSnapshot")) {
			t.setApplyConsistentSnapshot(Boolean.parseBoolean(project.property("applyConsistentSnapshot")))
		}
		if (project.hasProperty("awaitCompletion")) {
			t.setAwaitCompletion(Boolean.parseBoolean(project.property("awaitCompletion")))
		}
		if (project.hasProperty("stopJob")) {
			t.setStopJob(Boolean.parseBoolean(project.property("stopJob")))
		}
		if (project.hasProperty("jobName")) {
			t.setJobName(project.property("jobName"))
		}

		if (project.hasProperty("logBatches")) {
			t.addUrisReadyListeners(new QueryBatchListener() {
				@Override
				void processEvent(QueryBatch batch) {
					println String.format("Processed batch number [%d]; job results so far: [%d]", batch.getJobBatchNumber(),
					batch.getJobResultsSoFar())
				}
			})
		}
		
		return t
	}
}
