package com.marklogic.client.ext.datamovement;

import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.QueryBatcher;

/**
 * Receipt-style object for QueryBatcherTemplate methods. Intended to give the client control over how the job is stopped,
 * if it hasn't been already, as well as the JobTicket so that other job information can be retrieved.
 */
public class QueryBatcherJobTicket {

	private DataMovementManager dataMovementManager;
	private QueryBatcher queryBatcher;
	private JobTicket jobTicket;

	public QueryBatcherJobTicket(DataMovementManager dataMovementManager, QueryBatcher queryBatcher, JobTicket jobTicket) {
		this.dataMovementManager = dataMovementManager;
		this.queryBatcher = queryBatcher;
		this.jobTicket = jobTicket;
	}

	public DataMovementManager getDataMovementManager() {
		return dataMovementManager;
	}

	public QueryBatcher getQueryBatcher() {
		return queryBatcher;
	}

	public JobTicket getJobTicket() {
		return jobTicket;
	}
}
