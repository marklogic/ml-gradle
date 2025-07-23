/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.query.StructuredQueryDefinition;

/**
 * Builds a QueryBatcher based on an array of collection URIs.
 */
public class CollectionsQueryBatcherBuilder implements QueryBatcherBuilder {

	private String[] collectionUris;

	public CollectionsQueryBatcherBuilder(String... collectionUris) {
		this.collectionUris = collectionUris;
	}

	@Override
	public QueryBatcher buildQueryBatcher(DatabaseClient databaseClient, DataMovementManager dataMovementManager) {
		StructuredQueryDefinition query = databaseClient.newQueryManager().newStructuredQueryBuilder().collection(collectionUris);
		return dataMovementManager.newQueryBatcher(query);
	}
}
