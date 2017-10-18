package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.QueryRoleSetsManager;

public class ManageQueryRoleSetsTest extends AbstractManageResourceTest {
    @Override
    protected ResourceManager newResourceManager() {
        return new QueryRoleSetsManager(manageClient);
    }

    @Override
    protected Command newCommand() {
        return new DeployQueryRoleSetsCommand();
    }

	@Override
	protected String[] getResourceNames() {
		return new String[] { "[\"view-admin\"]" };
	}
}
