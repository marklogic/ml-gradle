package com.marklogic.mgmt.resource.security;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.rest.util.Fragment;
import com.marklogic.rest.util.ResourcesFragment;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

public class ProtectedPathManager extends AbstractResourceManager {
	public ProtectedPathManager(ManageClient client) {
		super(client);
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
		return getResourcesPath()  + "/" + getIdForPathExpression(resourceNameOrId) + "/properties";
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
		Fragment f = getAsXml();
		return f.elementExists(format(
			"/node()/*[local-name(.) = 'list-items']/node()[*[local-name(.) = 'nameref'] = '%s']",
			resourceNameOrId));
	}

	public String getIdForPathExpression(String pathExpression) {
		Fragment f = getAsXml();
		String xpath = "/node()/*[local-name(.) = 'list-items']/node()"
			+ "[*[local-name(.) = 'nameref'] = '%s']/*[local-name(.) = 'idref']";
		xpath = String.format(xpath, pathExpression);
		String id = f.getElementValue(xpath);
		if (id == null) {
			throw new RuntimeException("Could not find a protected path with a path-expression of: " + pathExpression);
		}
		return id;
	}

	@Override
	protected boolean useAdminUser() { return true; }


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
			return new ResourcesFragment(getManageClient().getXmlAsAdmin(getResourcesPath()));
		} catch (ResourceAccessException ex) {
			if (logger.isWarnEnabled()) {
				logger.warn("Unable to get list of protected paths, retrying; cause: " + ex.getMessage());
			}
			return new ResourcesFragment(getManageClient().getXmlAsAdmin(getResourcesPath()));
		}
	}

}
