package com.marklogic.appdeployer.export.security;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.export.impl.AbstractNamedResourceExporter;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.RoleManager;

import java.io.File;

public class RoleExporter extends AbstractNamedResourceExporter {

	public RoleExporter(ManageClient manageClient, String... usernames) {
		super(manageClient, usernames);
	}

	@Override
	protected ResourceManager newResourceManager(ManageClient manageClient) {
		return new RoleManager(manageClient);
	}

	@Override
	protected File getResourceDirectory(File baseDir) {
		return new ConfigDir(baseDir).getRolesDir();
	}

}
