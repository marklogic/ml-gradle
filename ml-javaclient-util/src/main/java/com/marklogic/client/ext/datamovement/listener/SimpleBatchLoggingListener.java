/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.datamovement.listener;

import com.marklogic.client.datamovement.QueryBatch;
import com.marklogic.client.datamovement.QueryBatchListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple listener that logs each batch. By default, this will log a status message to stdout. Use setUseLogger(true)
 * to force this to use SLF4J instead, logging at the info level.
 */
public class SimpleBatchLoggingListener implements QueryBatchListener {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	private boolean useLogger;

	public SimpleBatchLoggingListener() {
		this(false);
	}

	public SimpleBatchLoggingListener(boolean useLogger) {
		this.useLogger = useLogger;
	}

	@Override
	public void processEvent(QueryBatch queryBatch) {
		String message = String.format("Processed batch number [%d]; job results so far: [%d]",
			queryBatch.getJobBatchNumber(),
			queryBatch.getJobResultsSoFar());

		if (useLogger) {
			if (logger.isInfoEnabled()) {
				logger.info(message);
			}
		} else {
			System.out.println(message);
		}
	}

	public void setUseLogger(boolean useLogger) {
		this.useLogger = useLogger;
	}
}
