package com.marklogic.client.ext.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.ext.util.EvalResultIterator;

/**
 * Builds a QueryBatcher based on either an XQuery or Javascript query.
 * <p>
 * Because this will likely use either cts:uris or cts.uris, this class checks to see if your query starts with
 * "cts:" or "cts." but not "cts:uris" or "cts.uris". If it does, it assumes that your query is the 3rd argument for a
 * cts:uris or cts.uris call and wraps it appropriately. You can disable this behavior by calling
 * setWrapQueryIfAppropriate(false).
 */
public class UrisQueryQueryBatcherBuilder extends LoggingObject implements QueryBatcherBuilder {

	private String xquery;
	private String javascript;
	private boolean wrapQueryIfAppropriate = true;

	/**
	 * Looks for "cts:" in the query - if it's found, then assumes this is XQuery; otherwise, Javascript. If this isn't
	 * reliable for your query, use one of the static methods on this class to explicitly declare the type of your query.
	 *
	 * @param query
	 */
	public UrisQueryQueryBatcherBuilder(String query) {
		if (query.contains("cts:")) {
			xquery = query;
		} else {
			javascript = query;
		}
	}

	/**
	 * Empty constructor used by the static methods on this class.
	 */
	protected UrisQueryQueryBatcherBuilder() {
	}

	public static UrisQueryQueryBatcherBuilder withXquery(String xquery) {
		UrisQueryQueryBatcherBuilder b = new UrisQueryQueryBatcherBuilder();
		b.xquery = xquery;
		return b;
	}

	public static UrisQueryQueryBatcherBuilder withJavascript(String javascript) {
		UrisQueryQueryBatcherBuilder b = new UrisQueryQueryBatcherBuilder();
		b.javascript = javascript;
		return b;
	}

	@Override
	public QueryBatcher buildQueryBatcher(DatabaseClient databaseClient, DataMovementManager dataMovementManager) {
		ServerEvaluationCall call = databaseClient.newServerEval();
		if (javascript != null) {
			if (wrapQueryIfAppropriate) {
				javascript = wrapJavascriptIfAppropriate(javascript);
			}
			if (logger.isInfoEnabled()) {
				logger.info("Calling JavaScript: " + javascript);
			}
			call = call.javascript(javascript);
		} else if (xquery != null) {
			if (wrapQueryIfAppropriate) {
				xquery = wrapXqueryIfAppropriate(xquery);
			}
			if (logger.isInfoEnabled()) {
				logger.info("Calling XQuery: " + xquery);
			}
			call = call.xquery(xquery);
		} else {
			throw new IllegalStateException("Either xquery or javascript must be defined");
		}

		return dataMovementManager.newQueryBatcher(new EvalResultIterator(call.eval().iterator()));
	}

	protected String wrapXqueryIfAppropriate(String query) {
		if (query.startsWith("cts:") && !query.startsWith("cts:uris")) {
			return String.format("cts:uris((), (), %s)", query);
		}
		return query;
	}

	protected String wrapJavascriptIfAppropriate(String query) {
		if (query.startsWith("cts.") && !query.startsWith("cts.uris")) {
			return String.format("cts.uris(\"\", null, %s)", query);
		}
		return query;
	}

	public void setWrapQueryIfAppropriate(boolean wrapQueryIfAppropriate) {
		this.wrapQueryIfAppropriate = wrapQueryIfAppropriate;
	}
}
