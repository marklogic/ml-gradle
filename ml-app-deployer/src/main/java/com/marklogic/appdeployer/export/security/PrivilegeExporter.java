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
