package com.marklogic.appdeployer.export.security;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.export.AbstractNamedResourceExporter;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.UserManager;

import java.io.File;

public class UserExporter extends AbstractNamedResourceExporter {

	public UserExporter(ManageClient manageClient, String... usernames) {
		super(manageClient, usernames);
	}

	@Override
	protected ResourceManager newResourceManager(ManageClient manageClient) {
		return new UserManager(manageClient);
	}

	@Override
	protected File getResourceDirectory(File baseDir) {
		return new File(new ConfigDir(baseDir).getSecurityDir(), "users");
	}

	@Override
	protected String[] getExportMessages() {
		return new String[]{"The exported user files do not have a password in them, as passwords cannot be retrieved. Each user file needs a password added before it can be deployed."};
	}
}
