package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.SecureCredentialsManager;

public class DeploySecureCredentialsTest extends AbstractManageResourceTest {

	@Override
	protected ResourceManager newResourceManager() {
		return new SecureCredentialsManager(manageClient);
	}

	@Override
	protected Command newCommand() {
		return new DeploySecureCredentialsCommand();
	}

	@Override
	protected String[] getResourceNames() {
		return new String[]{"sec-creds1"};
	}
}
