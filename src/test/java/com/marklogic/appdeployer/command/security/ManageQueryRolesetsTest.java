package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.QueryRolesetsManager;

public class ManageQueryRolesetsTest extends AbstractManageResourceTest {

    @Override
    protected ResourceManager newResourceManager() {
        return new QueryRolesetsManager(manageClient);
    }

    @Override
    protected Command newCommand() {
        return new DeployQueryRolesetsCommand();
    }

	@Override
	protected String[] getResourceNames() {
		return new String[] { "[\"view-admin\"]" };
	}
}
