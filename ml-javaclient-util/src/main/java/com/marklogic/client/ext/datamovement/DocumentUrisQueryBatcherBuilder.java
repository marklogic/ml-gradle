/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.datamovement;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.query.StructuredQueryDefinition;

/**
 * Builds a QueryBatcher based on an array of document URIs.
 */
public class DocumentUrisQueryBatcherBuilder implements QueryBatcherBuilder {

	private String[] documentUris;

	public DocumentUrisQueryBatcherBuilder(String... documentUris) {
		this.documentUris = documentUris;
	}

	@Override
	public QueryBatcher buildQueryBatcher(DatabaseClient databaseClient, DataMovementManager dataMovementManager) {
		StructuredQueryDefinition query = databaseClient.newQueryManager().newStructuredQueryBuilder().document(documentUris);
		return dataMovementManager.newQueryBatcher(query);
	}
}
