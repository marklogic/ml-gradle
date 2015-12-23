package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.mgmt.ResourceManager;
import com.marklogic.mgmt.security.PrivilegeManager;

/**
 * TODO Unable to update a privilege of kind "uri".
 */
public class ManagePrivilegesTest extends AbstractManageResourceTest {

    @Override
    protected ResourceManager newResourceManager() {
        return new PrivilegeManager(manageClient);
    }

    @Override
    protected Command newCommand() {
        return new DeployPrivilegesCommand();
    }

    @Override
    protected String[] getResourceNames() {
        return new String[] { "sample-app-execute-1", "sample-app-execute-2" };
    }

}
