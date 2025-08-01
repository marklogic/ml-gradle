/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.datamovement.QueryBatcherJobTicket;

public interface QueryBatcherJob {

	QueryBatcherJobTicket run(DatabaseClient databaseClient);

}
