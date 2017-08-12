package com.marklogic.client.ext.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.query.StructuredQueryDefinition;

/**
 * Builds a QueryBatcher based on an array of document URIs.
 */
public class UrisQueryBatcherBuilder implements QueryBatcherBuilder {

	private String[] documentUris;

	public UrisQueryBatcherBuilder(String... documentUris) {
		this.documentUris = documentUris;
	}

	@Override
	public QueryBatcher buildQueryBatcher(DatabaseClient databaseClient, DataMovementManager dataMovementManager) {
		StructuredQueryDefinition query = databaseClient.newQueryManager().newStructuredQueryBuilder().document(documentUris);
		return dataMovementManager.newQueryBatcher(query);
	}
}
