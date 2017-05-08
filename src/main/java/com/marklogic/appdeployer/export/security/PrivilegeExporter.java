package com.marklogic.appdeployer.export.security;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.export.AbstractNamedResourceExporter;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ResourceManager;
import com.marklogic.mgmt.security.PrivilegeManager;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class PrivilegeExporter extends AbstractNamedResourceExporter {

	private List<String> uriPrivilegeNames;
	private boolean removeRoles = true;

	public PrivilegeExporter(ManageClient manageClient, String... privilegeNames) {
		super(manageClient, privilegeNames);
	}

	/**
	 * Privileges are assumed to be "execute" by default, as the Manage API needs to know if a privilege is "execute"
	 * or "uri".
	 *
	 * @param privilegeNames
	 */
	public void setUriPrivilegeNames(String... privilegeNames) {
		uriPrivilegeNames = Arrays.asList(privilegeNames);
	}

	@Override
	protected String beforeResourceWrittenToFile(String resourceName, String payload) {
		return isRemoveRoles() ? removeJsonKeyFromPayload(payload, "role") : payload;
	}

	@Override
	protected String[] getExportMessages() {
		return new String[] {"The 'role' key was removed from each exported privilege so that privileges can be deployed before roles."};
	}

	@Override
	protected String[] getResourceUrlParams(String resourceName) {
		String[] params = new String[2];
		params[0] = "kind";
		params[1] = uriPrivilegeNames != null && uriPrivilegeNames.contains(resourceName) ? "uri" : "execute";
		return params;
	}

	@Override
	protected ResourceManager newResourceManager(ManageClient manageClient) {
		return new PrivilegeManager(manageClient);
	}

	@Override
	protected File getResourceDirectory(File baseDir) {
		return new File(new ConfigDir(baseDir).getSecurityDir(), "privileges");
	}

	public boolean isRemoveRoles() {
		return removeRoles;
	}

	public void setRemoveRoles(boolean removeRoles) {
		this.removeRoles = removeRoles;
	}
}
