/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.resource.cpf;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.AbstractResourceManager;

/**
 * Requires a database ID or name for constructing the endpoint for each kind of CPF resource.
 */
public abstract class AbstractCpfResourceManager extends AbstractResourceManager {

	private String databaseIdOrName;

	public AbstractCpfResourceManager(ManageClient client, String databaseIdOrName) {
		super(client);
		this.databaseIdOrName = databaseIdOrName;
	}

	@Override
	public String getResourcesPath() {
		return format("/manage/v2/databases/%s/%ss", databaseIdOrName, getResourceName());
	}

	public String getDatabaseIdOrName() {
		return databaseIdOrName;
	}
}
