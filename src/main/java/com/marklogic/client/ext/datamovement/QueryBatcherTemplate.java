package com.marklogic.client.ext.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.QueryBatchListener;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.query.RawStructuredQueryDefinition;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryDefinition;

import java.util.Iterator;

/**
 * Spring-style Template class for simplifying common usages of QueryBatcher. Threadsafe, at least as long as
 * DatabaseClient and DataMovementManager are threadsafe.
 */
public class QueryBatcherTemplate extends LoggingObject {

	private DatabaseClient databaseClient;
	private DataMovementManager dataMovementManager;
	private int threadCount = 8;
	private int batchSize;
	private boolean applyConsistentSnapshot = true;
	private boolean awaitCompletion = true;
	private boolean stopJob = true;

	public QueryBatcherTemplate(DatabaseClient databaseClient) {
		this.databaseClient = databaseClient;
		this.dataMovementManager = this.databaseClient.newDataMovementManager();
	}

	/**
	 * Apply the given listener on batches of documents from the given set of collection URIs.
	 *
	 * @param listener
	 * @param collectionUris
	 * @return
	 */
	public QueryBatcherJobTicket applyOnCollections(QueryBatchListener listener, String... collectionUris) {
		return applyOnStructuredQuery(listener, databaseClient.newQueryManager().newStructuredQueryBuilder().collection(collectionUris));
	}

	/**
	 * Apply the given listener on batches of documents from the given set of document URIs.
	 *
	 * @param listener
	 * @param documentUris
	 * @return
	 */
	public QueryBatcherJobTicket applyOnDocuments(QueryBatchListener listener, String... documentUris) {
		return applyOnStructuredQuery(listener, databaseClient.newQueryManager().newStructuredQueryBuilder().document(documentUris));
	}

	/**
	 * Apply the given listener on batches on documents matching the given structured query.
	 *
	 * @param listener
	 * @param queryDefinition
	 * @return
	 */
	public QueryBatcherJobTicket applyOnStructuredQuery(QueryBatchListener listener, StructuredQueryDefinition queryDefinition) {
		return apply(listener, dataMovementManager.newQueryBatcher(queryDefinition));
	}

	/**
	 * Apply the given listener on batches on documents matching the given raw structured query.
	 *
	 * @param listener
	 * @param queryDefinition
	 * @return
	 */
	public QueryBatcherJobTicket applyOnRawStructuredQuery(QueryBatchListener listener, RawStructuredQueryDefinition queryDefinition) {
		return apply(listener, dataMovementManager.newQueryBatcher(queryDefinition));
	}

	/**
	 * Apply the given listener on batches on documents matching the given string query.
	 *
	 * @param listener
	 * @param queryDefinition
	 * @return
	 */
	public QueryBatcherJobTicket applyOnStringQuery(QueryBatchListener listener, StringQueryDefinition queryDefinition) {
		return apply(listener, dataMovementManager.newQueryBatcher(queryDefinition));
	}

	/**
	 * Apply the given listener on batches on documents matching the given raw combined query.
	 *
	 * @param listener
	 * @param queryDefinition
	 * @return
	 */
	public QueryBatcherJobTicket applyOnRawCombinedQuery(QueryBatchListener listener, RawCombinedQueryDefinition queryDefinition) {
		return apply(listener, dataMovementManager.newQueryBatcher(queryDefinition));
	}

	/**
	 * Apply the given listener on batches on documents matching the URIs from the given iterator.
	 *
	 * @param listener
	 * @param uriIterator
	 * @return
	 */
	public QueryBatcherJobTicket applyOnIterator(QueryBatchListener listener, Iterator<String> uriIterator) {
		return apply(listener, dataMovementManager.newQueryBatcher(uriIterator));
	}

	/**
	 * Apply the given listener with the given QueryBatcher. The QueryBatcher should have been constructed via the
	 * DatabaseClient that was used to instantiate this class.
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
	 * @param listener
	 * @param queryBatcher
	 * @return
	 */
	public QueryBatcherJobTicket apply(QueryBatchListener listener, QueryBatcher queryBatcher) {
		if (threadCount > 0) {
			queryBatcher.withThreadCount(threadCount);
		}
		if (batchSize > 0) {
			queryBatcher.withBatchSize(batchSize);
		}
		if (applyConsistentSnapshot) {
			queryBatcher.withConsistentSnapshot();
		}

		queryBatcher.onUrisReady(listener);
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
	 * If set to above zero, then each constructed QueryBatcher will use the given thread count.
	 *
	 * @param threadCount
	 */
	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	/**
	 * If set to above zero, then each constructed QueryBatcher will use the given batch size.
	 *
	 * @param batchSize
	 */
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
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
}
