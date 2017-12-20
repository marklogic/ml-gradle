package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.datamovement.QueryBatchListener;

/**
 * Simple job implementation that allows for adding zero or more "URIs ready" listeners.
 */
public class SimpleQueryBatcherJob extends AbstractQueryBatcherJob {

	public SimpleQueryBatcherJob(QueryBatchListener... listeners) {
		for (QueryBatchListener listener : listeners) {
			addUrisReadyListener(listener);
		}
	}

	@Override
	protected String getJobDescription() {
		return "Generic query batcher job";
	}
}
