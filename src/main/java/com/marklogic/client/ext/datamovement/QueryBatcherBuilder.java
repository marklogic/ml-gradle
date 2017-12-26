package com.marklogic.client.ext.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.QueryBatcher;

/**
 * Abstracts away how a QueryBatcher is constructed.
 */
public interface QueryBatcherBuilder {

	/**
	 * @param databaseClient      typically needed for constructing a QueryDefinition
	 * @param dataMovementManager
	 * @return
	 */
	QueryBatcher buildQueryBatcher(DatabaseClient databaseClient, DataMovementManager dataMovementManager);
}
