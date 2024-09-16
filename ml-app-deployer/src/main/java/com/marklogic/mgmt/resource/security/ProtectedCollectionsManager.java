/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
