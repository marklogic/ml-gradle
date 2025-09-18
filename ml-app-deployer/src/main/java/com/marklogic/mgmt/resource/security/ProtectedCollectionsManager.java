/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.resource.security;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.AbstractResourceManager;

public class ProtectedCollectionsManager extends AbstractResourceManager {

    public ProtectedCollectionsManager(ManageClient client) {
        super(client);
    }

	@Override
	protected boolean useSecurityUser() {
		return true;
	}

    @Override
    public String getResourcesPath() {
        return "/manage/v2/protected-collections";
    }

    @Override
    protected String getIdFieldName() {
        return "collection";
    }

    @Override
    public String getPropertiesPath(String resourceNameOrId, String... resourceUrlParams) {
        return getResourcesPath() + "/properties?collection=" + resourceNameOrId;
    }

    @Override
    public String getResourcePath(String resourceNameOrId, String... resourceUrlParams) {
        return getResourcesPath() + "?collection=" + resourceNameOrId;
    }
}
