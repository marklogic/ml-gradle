package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.*;
import com.marklogic.client.ext.datamovement.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides basic plumbing for implementing QueryBatcherJob.
 */
public abstract class AbstractQueryBatcherJob extends BatcherConfig implements QueryBatcherJob {

	private List<QueryBatchListener> urisReadyListeners;
	private List<QueryFailureListener> queryFailureListeners;

	private boolean applyConsistentSnapshot = false;
	private boolean awaitCompletion = true;
	private boolean stopJobAfterCompletion = true;

	// A client can provide its own DataMovementManager to be reused
	private DataMovementManager dataMovementManager;

	// A client can provide its own QueryBatcherBuilder in case it doesn't want to use the "where" properties
	private QueryBatcherBuilder queryBatcherBuilder;

	private String[] whereUris;
	private String[] whereCollections;
	private String whereUriPattern;
	private String whereUrisQuery;

	/**
	 * @return a description of the job that is useful for logging purposes.
	 */
	protected abstract String getJobDescription();

	@Override
	public QueryBatcherJobTicket run(DatabaseClient databaseClient) {
		DataMovementManager dmm = this.dataMovementManager != null ? this.dataMovementManager : databaseClient.newDataMovementManager();

		String jobDescription = getJobDescription();
		if (jobDescription != null && logger.isInfoEnabled()) {
			logger.info(jobDescription);
		}

		QueryBatcherBuilder builder = newQueryBatcherBuilder();
		QueryBatcher queryBatcher = builder.buildQueryBatcher(databaseClient, dmm);

		prepareQueryBatcher(queryBatcher);

		JobTicket jobTicket = dmm.startJob(queryBatcher);

		if (awaitCompletion) {
			queryBatcher.awaitCompletion();
			if (stopJobAfterCompletion) {
				dmm.stopJob(queryBatcher);
			}
			if (jobDescription != null && logger.isInfoEnabled()) {
				logger.info("Completed: " + jobDescription);
			}
		}

		return new QueryBatcherJobTicket(dmm, queryBatcher, jobTicket);
	}

	/**
	 * Can be overridden by the subclass to prepare the QueryBatcher before the job is started.
	 *
	 * @param queryBatcher
	 */
	protected void prepareQueryBatcher(QueryBatcher queryBatcher) {
		super.prepareBatcher(queryBatcher);

		if (applyConsistentSnapshot) {
			queryBatcher.withConsistentSnapshot();
		}

		if (urisReadyListeners != null) {
			for (QueryBatchListener listener : urisReadyListeners) {
				queryBatcher.onUrisReady(listener);
			}
		}

		if (queryFailureListeners != null) {
			for (QueryFailureListener listener : queryFailureListeners) {
				queryBatcher.onQueryFailure(listener);
			}
		}
	}

	/**
	 * For subclasses to use to construct a QueryBatcherBuilder based on the "where" properties that have been set.
	 *
	 * @return
	 */
	protected QueryBatcherBuilder newQueryBatcherBuilder() {
		if (queryBatcherBuilder != null) {
			return queryBatcherBuilder;
		}

		if (isWherePropertySet()) {
			if (whereUris != null && whereUris.length > 0) {
				return new DocumentUrisQueryBatcherBuilder(whereUris);
			}
			if (whereCollections != null) {
				return new CollectionsQueryBatcherBuilder(whereCollections);
			}
			if (whereUriPattern != null) {
				return new UriPatternQueryBatcherBuilder(whereUriPattern);
			}
			if (whereUrisQuery != null) {
				return new UrisQueryQueryBatcherBuilder(whereUrisQuery);
			}
		}
		throw new IllegalArgumentException("No 'where' property has been set, unable to construct a QueryBatcherBuilder");
	}

	/**
	 * @return an expression describing the query, based on the "where" properties in this class, that will be used to
	 * select documents
	 */
	protected String getQueryDescription() {
		if (this.queryBatcherBuilder != null) {
			return "with custom query";
		}

		if (isWherePropertySet()) {
			if (whereUris != null && whereUris.length > 0) {
				return "with URIs " + Arrays.asList(whereUris);
			} else if (whereCollections != null && whereCollections.length > 0) {
				return "in collections " + Arrays.asList(this.whereCollections);
			} else if (whereUriPattern != null) {
				return "matching URI pattern [" + whereUriPattern + "]";
			} else if (whereUrisQuery != null) {
				return "matching URIs query [" + whereUrisQuery + "]";
			}
		}
		throw new IllegalArgumentException("No 'where' property has been set, unable to construct a description of the query");
	}

	protected boolean isWherePropertySet() {
		return
			(whereUris != null && whereUris.length > 0)
				|| (whereCollections != null && whereCollections.length > 0)
				|| whereUriPattern != null
				|| whereUrisQuery != null;
	}

	public void addUrisReadyListener(QueryBatchListener listener) {
		if (urisReadyListeners == null) {
			urisReadyListeners = new ArrayList<>();
		}
		urisReadyListeners.add(listener);
	}

	public void addQueryFailureListener(QueryFailureListener listener) {
		if (queryFailureListeners == null) {
			queryFailureListeners = new ArrayList<>();
		}
		queryFailureListeners.add(listener);
	}


	public String[] getWhereCollections() {
		return whereCollections;
	}

	public AbstractQueryBatcherJob setWhereCollections(String... whereCollections) {
		this.whereCollections = whereCollections;
		return this;
	}

	public String getWhereUriPattern() {
		return whereUriPattern;
	}

	public AbstractQueryBatcherJob setWhereUriPattern(String whereUriPattern) {
		this.whereUriPattern = whereUriPattern;
		return this;
	}

	public String getWhereUrisQuery() {
		return whereUrisQuery;
	}

	public AbstractQueryBatcherJob setWhereUrisQuery(String whereUrisQuery) {
		this.whereUrisQuery = whereUrisQuery;
		return this;
	}

	public String[] getWhereUris() {
		return whereUris;
	}

	public AbstractQueryBatcherJob setWhereUris(String... whereUris) {
		this.whereUris = whereUris;
		return this;
	}

	public List<QueryBatchListener> getUrisReadyListeners() {
		return urisReadyListeners;
	}

	public AbstractQueryBatcherJob setUrisReadyListeners(List<QueryBatchListener> urisReadyListeners) {
		this.urisReadyListeners = urisReadyListeners;
		return this;
	}

	public List<QueryFailureListener> getQueryFailureListeners() {
		return queryFailureListeners;
	}

	public AbstractQueryBatcherJob setQueryFailureListeners(List<QueryFailureListener> queryFailureListeners) {
		this.queryFailureListeners = queryFailureListeners;
		return this;
	}

	public boolean isApplyConsistentSnapshot() {
		return applyConsistentSnapshot;
	}

	public AbstractQueryBatcherJob setApplyConsistentSnapshot(boolean applyConsistentSnapshot) {
		this.applyConsistentSnapshot = applyConsistentSnapshot;
		return this;
	}

	public boolean isAwaitCompletion() {
		return awaitCompletion;
	}

	public AbstractQueryBatcherJob setAwaitCompletion(boolean awaitCompletion) {
		this.awaitCompletion = awaitCompletion;
		return this;
	}

	public boolean isStopJobAfterCompletion() {
		return stopJobAfterCompletion;
	}

	public AbstractQueryBatcherJob setStopJobAfterCompletion(boolean stopJobAfterCompletion) {
		this.stopJobAfterCompletion = stopJobAfterCompletion;
		return this;
	}

	public AbstractQueryBatcherJob setDataMovementManager(DataMovementManager dataMovementManager) {
		this.dataMovementManager = dataMovementManager;
		return this;
	}

	public AbstractQueryBatcherJob setQueryBatcherBuilder(QueryBatcherBuilder queryBatcherBuilder) {
		this.queryBatcherBuilder = queryBatcherBuilder;
		return this;
	}
}
