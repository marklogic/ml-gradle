/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.export.security;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.export.impl.AbstractNamedResourceExporter;
import com.marklogic.appdeployer.export.impl.ExportInputs;
import com.marklogic.appdeployer.export.impl.SimpleExportInputs;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.PrivilegeManager;

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
	protected File exportToFile(ResourceManager mgr, String resourceName, File resourceDir) {
		SimpleExportInputs inputs = new SimpleExportInputs(resourceName, "kind",
			uriPrivilegeNames != null && uriPrivilegeNames.contains(resourceName) ? "uri" : "execute");
		return super.exportToFile(mgr, inputs, resourceDir);
	}

	@Override
	protected String beforeResourceWrittenToFile(ExportInputs exportInputs, String payload) {
		return isRemoveRoles() ? removeJsonKeyFromPayload(payload, "role") : payload;
	}

	@Override
	protected String[] getExportMessages() {
		return new String[] {"The 'role' key was removed from each exported privilege so that privileges can be deployed before roles."};
	}

	@Override
	protected ResourceManager newResourceManager(ManageClient manageClient) {
		return new PrivilegeManager(manageClient);
	}

	@Override
	protected File getResourceDirectory(File baseDir) {
		return new ConfigDir(baseDir).getPrivilegesDir();
	}

	public boolean isRemoveRoles() {
		return removeRoles;
	}

	public void setRemoveRoles(boolean removeRoles) {
		this.removeRoles = removeRoles;
	}
}
