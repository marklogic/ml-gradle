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

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.rest.util.Fragment;
import com.marklogic.rest.util.ResourcesFragment;
import org.springframework.web.client.ResourceAccessException;

public class ProtectedPathManager extends AbstractResourceManager {
	public ProtectedPathManager(ManageClient client) {
		super(client);
	}

	@Override
	protected boolean useSecurityUser() {
		return true;
	}

	@Override
	public String getResourcesPath() {
		return "/manage/v2/protected-paths";
	}

	@Override
	protected String getResourceName() {
		return "protected-path";
	}

	@Override
	protected String getIdFieldName() {
		return "path-expression";
	}

	@Override
	public String getPropertiesPath(String resourceNameOrId, String... resourceUrlParams) {
		return getResourcesPath() + "/" + getIdForPathExpression(resourceNameOrId) + "/properties";
	}

	@Override
	public String getResourcePath(String resourceNameOrId, String... resourceUrlParams) {
		return getResourcesPath() + "/" + getIdForPathExpression(resourceNameOrId);
	}

	@Override
	protected String[] getDeleteResourceParams(String payload) {
		// We need to unprotect the path before deleting it
		// Otherwise we'll get a SEC-MUSTUNPROTECTPATH error
		return new String[]{"force", "true"};
	}

	@Override
	public boolean exists(String resourceNameOrId, String... resourceUrlParams) {
		if (logger.isInfoEnabled()) {
			logger.info("Checking for existence of resource: " + resourceNameOrId);
		}
		return getAsXml().elementExists(format(
			"/node()/*[local-name(.) = 'list-items']/node()[*[local-name(.) = 'nameref'] = '%s']",
			resourceNameOrId));
	}

	public String getIdForPathExpression(String pathExpression) {
		return getIdForPathExpression(pathExpression, getAsXml());
	}

	public String getIdForPathExpression(String pathExpression, Fragment resourcesXml) {
		String xpath = "/node()/*[local-name(.) = 'list-items']/node()"
			+ "[*[local-name(.) = 'nameref'] = '%s']/*[local-name(.) = 'idref']";
		xpath = String.format(xpath, pathExpression);
		String id = resourcesXml.getElementValue(xpath);
		if (id == null) {
			throw new RuntimeException("Could not find a protected path with a path-expression of: " + pathExpression);
		}
		return id;
	}

	/**
	 * Testing the deployment/undeployment of protected paths intermittently fails when performing a GET on the
	 * /manage/v2/protected-paths endpoint. A single retry seems to address the issue, though the cause is still
	 * unknown.
	 *
	 * @return ResourcesFragment
	 */
	@Override
	public ResourcesFragment getAsXml() {
		try {
			return new ResourcesFragment(getManageClient().getXmlAsSecurityUser(getResourcesPath()));
		} catch (ResourceAccessException ex) {
			if (logger.isWarnEnabled()) {
				logger.warn("Unable to get list of protected paths, retrying; cause: " + ex.getMessage());
			}
			return new ResourcesFragment(getManageClient().getXmlAsSecurityUser(getResourcesPath()));
		}
	}

}
