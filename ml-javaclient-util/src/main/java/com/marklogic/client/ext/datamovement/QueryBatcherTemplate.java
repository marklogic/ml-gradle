/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.query.RawStructuredQueryDefinition;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryDefinition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Spring-style Template class for simplifying common usages of QueryBatcher. Threadsafe, at least as long as
 * DatabaseClient and DataMovementManager are threadsafe.
 */
public class QueryBatcherTemplate extends BatcherConfig {

	private DatabaseClient databaseClient;
	private DataMovementManager dataMovementManager;
	private boolean applyConsistentSnapshot = true;
	private boolean awaitCompletion = true;
	private boolean stopJob = true;
	private List<QueryFailureListener> queryFailureListeners;
	private List<QueryBatchListener> urisReadyListeners;

	public QueryBatcherTemplate(DatabaseClient databaseClient) {
		this.databaseClient = databaseClient;
		this.dataMovementManager = this.databaseClient.newDataMovementManager();
	}

	/**
	 * Apply the given listener on batches of documents from the given set of collection URIs.
	 *
	 * @param urisReadyListener
	 * @param collectionUris
	 * @return
	 */
	public QueryBatcherJobTicket applyOnCollections(QueryBatchListener urisReadyListener, String... collectionUris) {
		return apply(urisReadyListener, new CollectionsQueryBatcherBuilder(collectionUris));
	}

	/**
	 * Apply the given listener on batches of documents from the given set of document URIs.
	 *
	 * @param urisReadyListener
	 * @param documentUris
	 * @return
	 */
	public QueryBatcherJobTicket applyOnDocumentUris(QueryBatchListener urisReadyListener, String... documentUris) {
		return apply(urisReadyListener, new DocumentUrisQueryBatcherBuilder(documentUris));
	}

	/**
	 * Apply the given listener on batches of documents with URIs matching the given URI pattern.
	 *
	 * @param urisReadyListener
	 * @param uriPattern
	 * @return
	 */
	public QueryBatcherJobTicket applyOnUriPattern(QueryBatchListener urisReadyListener, String uriPattern) {
		return apply(urisReadyListener, new UriPatternQueryBatcherBuilder(uriPattern));
	}

	/**
	 * Apply the given listener on batches of documents with URIs matching the given XQuery or JavaScript query.
	 *
	 * @param urisReadyListener
	 * @param xqueryOrJavascriptQuery
	 * @return
	 */
	public QueryBatcherJobTicket applyOnUrisQuery(QueryBatchListener urisReadyListener, String xqueryOrJavascriptQuery) {
		return apply(urisReadyListener, new UrisQueryQueryBatcherBuilder(xqueryOrJavascriptQuery));
	}

	/**
	 * Apply the given listener on batches on documents matching the given structured query.
	 *
	 * @param urisReadyListener
	 * @param queryDefinition
	 * @return
	 */
	public QueryBatcherJobTicket applyOnStructuredQuery(QueryBatchListener urisReadyListener, StructuredQueryDefinition queryDefinition) {
		return apply(urisReadyListener, dataMovementManager.newQueryBatcher(queryDefinition));
	}

	/**
	 * Apply the given listener on batches on documents matching the given raw structured query.
	 *
	 * @param urisReadyListener
	 * @param queryDefinition
	 * @return
	 */
	public QueryBatcherJobTicket applyOnRawStructuredQuery(QueryBatchListener urisReadyListener, RawStructuredQueryDefinition queryDefinition) {
		return apply(urisReadyListener, dataMovementManager.newQueryBatcher(queryDefinition));
	}

	/**
	 * Apply the given listener on batches on documents matching the given string query.
	 *
	 * @param urisReadyListener
	 * @param queryDefinition
	 * @return
	 */
	public QueryBatcherJobTicket applyOnStringQuery(QueryBatchListener urisReadyListener, StringQueryDefinition queryDefinition) {
		return apply(urisReadyListener, dataMovementManager.newQueryBatcher(queryDefinition));
	}

	/**
	 * Apply the given listener on batches on documents matching the given raw combined query.
	 *
	 * @param urisReadyListener
	 * @param queryDefinition
	 * @return
	 */
	public QueryBatcherJobTicket applyOnRawCombinedQuery(QueryBatchListener urisReadyListener, RawCombinedQueryDefinition queryDefinition) {
		return apply(urisReadyListener, dataMovementManager.newQueryBatcher(queryDefinition));
	}

	/**
	 * Apply the given listener on batches on documents matching the URIs from the given iterator.
	 *
	 * @param urisReadyListener
	 * @param uriIterator
	 * @return
	 */
	public QueryBatcherJobTicket applyOnIterator(QueryBatchListener urisReadyListener, Iterator<String> uriIterator) {
		return apply(urisReadyListener, dataMovementManager.newQueryBatcher(uriIterator));
	}

	/**
	 * Apply the given listener on batches of documents returning by the QueryBatcher that's constructed by the
	 * given QueryBatcherBuilder.
	 *
	 * @param urisReadyListener
	 * @param queryBatcherBuilder
	 * @return
	 */
	public QueryBatcherJobTicket apply(QueryBatchListener urisReadyListener, QueryBatcherBuilder queryBatcherBuilder) {
		return apply(urisReadyListener, queryBatcherBuilder.buildQueryBatcher(databaseClient, dataMovementManager));
	}

	/**
	 * Apply the given listener with the given QueryBatcher. The QueryBatcher should have been constructed via the
	 * DatabaseClient that was used to instantiate this class.
	 * <p>
	 * The given listener can be null. In such a scenario, it's expected that listeners have been defined on this class
	 * via the setQueryBatchListeners method, or are already included in the given QueryBatcher.
	 * </p>
	 * <p>
	 * Notes on how the job is run:
	 * <ol>
	 * <li>If awaitCompletion is set to true (the default), then awaitCompletion() is invoked on the QueryBatcher.</li>
	 * <li>if stopJob is set to true (the default), then stopJob() is invoked on the DataMovementManager with the QueryBatcher.</li>
	 * <li>If neither awaitCompletion or stopJob is set to true, you can still call awaitCompletion() when you want
	 * via the QueryBatcher returned in the QueryBatcherJobTicket, and you can still call stopJob by calling
	 * getDataMovementManager() on this class.</li>
	 * <li>It is highly unlikely that you want awaitCompletion set to false and stopJob set to true - the job will most
	 * likely be stopped with URIs that have not be processed yet.</li>
	 * </ol>
	 *
	 * @param urisReadyListener
	 * @param queryBatcher
	 * @return
	 */
	public QueryBatcherJobTicket apply(QueryBatchListener urisReadyListener, QueryBatcher queryBatcher) {
		prepareBatcher(queryBatcher);

		if (applyConsistentSnapshot) {
			queryBatcher.withConsistentSnapshot();
		}

		if (urisReadyListeners != null) {
			queryBatcher.setUrisReadyListeners(urisReadyListeners.toArray(new QueryBatchListener[]{}));
		}

		if (urisReadyListener != null) {
			queryBatcher.onUrisReady(urisReadyListener);
		}

		if (queryFailureListeners != null) {
			// If listeners already exist, add the ones configured on this class before the existing ones
			QueryFailureListener[] existingListeners = queryBatcher.getQueryFailureListeners();
			if (existingListeners == null || existingListeners.length == 0) {
				queryBatcher.setQueryFailureListeners(queryFailureListeners.toArray(new QueryFailureListener[]{}));
			} else {
				List<QueryFailureListener> newListeners = new ArrayList<>();
				newListeners.addAll(queryFailureListeners);
				for (QueryFailureListener listener : existingListeners) {
					newListeners.add(listener);
				}
				queryBatcher.setQueryFailureListeners(newListeners.toArray(new QueryFailureListener[]{}));
			}
		}

		JobTicket jobTicket = dataMovementManager.startJob(queryBatcher);

		if (awaitCompletion) {
			queryBatcher.awaitCompletion();
		}
		if (stopJob) {
			dataMovementManager.stopJob(queryBatcher);
		}

		return new QueryBatcherJobTicket(dataMovementManager, queryBatcher, jobTicket);
	}

	/**
	 * If set to true, then each constructed QueryBatcher will apply a consistent snapshot. Defaults to true.
	 *
	 * @param applyConsistentSnapshot
	 */
	public void setApplyConsistentSnapshot(boolean applyConsistentSnapshot) {
		this.applyConsistentSnapshot = applyConsistentSnapshot;
	}

	/**
	 * If set to true, then each constructed QueryBatcher will wait for a job to complete. Defaults to true.
	 *
	 * @param awaitCompletion
	 */
	public void setAwaitCompletion(boolean awaitCompletion) {
		this.awaitCompletion = awaitCompletion;
	}

	/**
	 * If set to true, then each job will be stopped before a QueryBatcherJobTicket is returned.
	 *
	 * @param stopJob
	 */
	public void setStopJob(boolean stopJob) {
		this.stopJob = stopJob;
	}

	/**
	 * @return the instance of DataMovementManager that was constructed when this class was instantiated
	 */
	public DataMovementManager getDataMovementManager() {
		return dataMovementManager;
	}

	/**
	 * @return the DatabaseClient that was used to instantiate this class
	 */
	public DatabaseClient getDatabaseClient() {
		return databaseClient;
	}

	public void addQueryFailureListeners(QueryFailureListener... listeners) {
		if (this.queryFailureListeners == null) {
			this.queryFailureListeners = new ArrayList<>();
		}
		for (QueryFailureListener listener : listeners) {
			this.queryFailureListeners.add(listener);
		}
	}

	public void setQueryFailureListeners(QueryFailureListener... listeners) {
		this.queryFailureListeners = new ArrayList<>();
		for (QueryFailureListener listener : listeners) {
			this.queryFailureListeners.add(listener);
		}
	}

	public void addUrisReadyListeners(QueryBatchListener... listeners) {
		if (this.urisReadyListeners == null) {
			this.urisReadyListeners = new ArrayList<>();
		}
		for (QueryBatchListener listener : listeners) {
			this.urisReadyListeners.add(listener);
		}
	}

	public void setUrisReadyListeners(QueryBatchListener... listeners) {
		this.urisReadyListeners = new ArrayList<>();
		for (QueryBatchListener listener : listeners) {
			this.urisReadyListeners.add(listener);
		}
	}
}
