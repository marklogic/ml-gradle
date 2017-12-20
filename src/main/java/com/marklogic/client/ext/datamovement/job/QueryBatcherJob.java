package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.datamovement.QueryBatcherJobTicket;

public interface QueryBatcherJob {

	QueryBatcherJobTicket run(DatabaseClient databaseClient);

}
