package com.marklogic.client.ext.datamovement.listener;

import com.marklogic.client.datamovement.QueryBatch;
import com.marklogic.client.datamovement.QueryBatchListener;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.ext.util.SequenceUtil;

public abstract class AbstractCollectionsListener extends LoggingObject implements QueryBatchListener {

	private String[] collections;

	public AbstractCollectionsListener(String... collections) {
		this.collections = collections;
	}

	protected abstract String getXqueryFunction();

	@Override
	public void processEvent(QueryBatch queryBatch) {
		String[] uris = queryBatch.getItems();
		String collectionSequence = SequenceUtil.arrayToSequence(collections);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < uris.length; i++) {
			sb.append("declare variable $uri" + i + " external;\n");
		}
		String function = getXqueryFunction();
		for (int i = 0; i < uris.length; i++) {
			if (i > 0) {
				sb.append(",\n");
			}
			sb.append(String.format("%s($uri%d, %s)", function, i, collectionSequence));
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Executing: " + sb);
		}
		ServerEvaluationCall call = queryBatch.getClient().newServerEval();
		call.xquery(sb.toString());
		for (int i = 0; i < uris.length; i++) {
			call.addVariable("uri" + i, uris[i]);
		}
		call.eval();
	}
}
