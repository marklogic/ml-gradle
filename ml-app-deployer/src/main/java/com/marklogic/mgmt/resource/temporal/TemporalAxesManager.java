/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.resource.temporal;

import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.mgmt.ManageClient;

public class TemporalAxesManager extends AbstractResourceManager {

	private String databaseIdOrName;

	public TemporalAxesManager(ManageClient client, String databaseIdOrName) {
		super(client);
		setUpdateAllowed(false);
		this.databaseIdOrName = databaseIdOrName;
	}

	@Override
	public String getResourcesPath() {
		return format("/manage/v2/databases/%s/temporal/axes", databaseIdOrName);
	}

	@Override
	protected String getIdFieldName() {
		return "axis-name";
	}
}
