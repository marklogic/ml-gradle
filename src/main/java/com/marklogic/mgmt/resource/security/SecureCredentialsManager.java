package com.marklogic.mgmt.resource.security;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.AbstractResourceManager;

public class SecureCredentialsManager extends AbstractResourceManager {
	public SecureCredentialsManager(ManageClient client) {
		super(client);
	}

	@Override
	public String getResourcesPath() {
		return "/manage/v2/credentials/secure";
	}

	@Override
	protected String getIdFieldName() {
		return "name";
	}
}
