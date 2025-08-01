/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.resource.flexrep;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.AbstractResourceManager;

public class PullManager extends AbstractResourceManager {

	private String databaseIdOrName;

	public PullManager(ManageClient client, String databaseIdOrName) {
		super(client);
		this.databaseIdOrName = databaseIdOrName;
	}

	@Override
	protected String getIdFieldName() {
		return "pull-name";
	}

	@Override
	public String getResourcesPath() {
		return format("/manage/v2/databases/%s/flexrep/pulls", databaseIdOrName);
	}
}
