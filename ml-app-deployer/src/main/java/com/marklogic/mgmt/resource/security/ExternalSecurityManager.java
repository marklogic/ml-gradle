/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.resource.security;

import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.mgmt.ManageClient;

public class ExternalSecurityManager extends AbstractResourceManager {

    public ExternalSecurityManager(ManageClient client) {
        super(client);
    }

	@Override
	protected boolean useSecurityUser() {
		return true;
	}

    @Override
    public String getResourcesPath() {
        return "/manage/v2/external-security";
    }

    @Override
    protected String getIdFieldName() {
        return "external-security-name";
    }

}
