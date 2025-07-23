/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.datamovement.DeleteListener;

/**
 * Simple job for deleting documents. Uses the DMSDK DeleteListener and requires a "where" property to be set to
 * specify which documents should be deleted.
 */
public class DeleteJob extends AbstractQueryBatcherJob {

	public DeleteJob() {
		setRequireWhereProperty(true);
		this.addUrisReadyListener(new DeleteListener());
	}

	@Override
	protected String getJobDescription() {
		return "Deletes documents matching the query defined by a 'where' property";
	}
}
