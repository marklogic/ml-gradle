package com.marklogic.client.ext.datamovement.listener;

import com.marklogic.client.datamovement.QueryBatch;
import com.marklogic.client.datamovement.QueryBatchListener;
import com.marklogic.client.ext.util.SequenceUtil;
import com.marklogic.client.ext.helper.LoggingObject;

public abstract class AbstractPermissionsListener extends LoggingObject implements QueryBatchListener {

	private String[] rolesAndCapabilities;

	public AbstractPermissionsListener(String... rolesAndCapabilities) {
		this.rolesAndCapabilities = rolesAndCapabilities;
	}

	protected abstract String getXqueryFunction();

	@Override
	public void processEvent(QueryBatch queryBatch) {
		String[] uris = queryBatch.getItems();
		StringBuilder sb = new StringBuilder(SequenceUtil.arrayToSequence(uris));
		sb.append(String.format(" ! %s(., ", getXqueryFunction()));
		sb.append(buildPermissions(this.rolesAndCapabilities));
		sb.append(")");
		queryBatch.getClient().newServerEval().xquery(sb.toString()).evalAs(String.class);
	}

	protected String buildPermissions(String[] rolesAndCapabilities) {
		StringBuilder sb = new StringBuilder("(");
		for (int i = 0; i < rolesAndCapabilities.length; i += 2) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(String.format("xdmp:permission(\"%s\", \"%s\")", rolesAndCapabilities[i], rolesAndCapabilities[i + 1]));
		}
		sb.append(")");
		return sb.toString();
	}
}

