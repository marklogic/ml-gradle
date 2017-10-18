package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.ProtectedPathManager;

public class ManageProtectedPathsTest extends AbstractManageResourceTest {
	@Override
	protected ResourceManager newResourceManager() {
        return new ProtectedPathManager(manageClient);
	}

	@Override
	protected Command newCommand() {
		return new DeployProtectedPathCommand();
	}

	@Override
	protected String[] getResourceNames() {
		return new String[] { "/test:element" };
	}

}
