/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.resource.security;

import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.mgmt.ManageClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
        return getResourcesPath() + "/properties?collection=" + encodeCollectionName(resourceNameOrId);
    }

    @Override
    public String getResourcePath(String resourceNameOrId, String... resourceUrlParams) {
        return getResourcesPath() + "?collection=" + encodeCollectionName(resourceNameOrId);
    }

	/**
	 * For most Manage API resources, MarkLogic prohibits characters that require URL encoding. Collection names are
	 * an exception though and thus require URL encoding so that their names can be used in a querystring.
	 *
	 * @param collectionName
	 * @return
	 */
	private String encodeCollectionName(String collectionName) {
		try {
			return URLEncoder.encode(collectionName, "utf-8");
		} catch (UnsupportedEncodingException e) {
			logger.warn(format("Unable to encode collection: %s; will include un-encoded collection name in " +
				"querystring; cause: %s", collectionName, e.getMessage()));
			return collectionName;
		}
	}
}
