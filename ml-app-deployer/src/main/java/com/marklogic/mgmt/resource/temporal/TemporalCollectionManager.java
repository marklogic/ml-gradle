/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.resource.temporal;

import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.mgmt.ManageClient;

public class TemporalCollectionManager extends AbstractResourceManager {

	private String databaseIdOrName;

	public TemporalCollectionManager(ManageClient client, String databaseIdOrName) {
		super(client);
		this.databaseIdOrName = databaseIdOrName;
	}

	@Override
	public String getResourcesPath() {
		return format("/manage/v2/databases/%s/temporal/collections", databaseIdOrName);
	}

	@Override
	public String getResourcePath(String resourceNameOrId, String... resourceUrlParams) {
		resourceNameOrId = encodeResourceId(resourceNameOrId);
		return appendParamsAndValuesToPath(format("%s?collection=%s", getResourcesPath(), resourceNameOrId), resourceUrlParams);
	}

	@Override
	public String getPropertiesPath(String resourceNameOrId, String... resourceUrlParams) {
		return appendParamsAndValuesToPath(format("%s/properties?collection=%s", getResourcesPath(),resourceNameOrId),
			resourceUrlParams);
	}

	@Override
	protected String getIdFieldName() {
		return "collection-name";
	}
}
