package com.marklogic.client.ext.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.eval.EvalResult;
import com.marklogic.client.ext.util.EvalResultIterator;

import java.util.Iterator;

/**
 * Builds a QueryBatcher based on a URI pattern that is fed into cts:uri-match via an eval call. Note that cts:uri-match
 * may not always scale as well as a cts:uris query will.
 */
public class UriPatternQueryBatcherBuilder implements QueryBatcherBuilder {

	private String uriPattern;

	public UriPatternQueryBatcherBuilder(String uriPattern) {
		this.uriPattern = uriPattern;
	}

	@Override
	public QueryBatcher buildQueryBatcher(DatabaseClient databaseClient, DataMovementManager dataMovementManager) {
		final Iterator<EvalResult> evalResults = databaseClient.newServerEval().xquery(String.format("cts:uri-match('%s')", uriPattern)).eval().iterator();
		return dataMovementManager.newQueryBatcher(new EvalResultIterator(evalResults));
	}
}
