package com.rjrudin.marklogic.appdeployer.command.security;

import com.rjrudin.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.rjrudin.marklogic.appdeployer.command.Command;
import com.rjrudin.marklogic.appdeployer.command.security.CreatePrivilegesCommand;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.security.PrivilegeManager;

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
        return new CreatePrivilegesCommand();
    }

    @Override
    protected String[] getResourceNames() {
        return new String[] { "sample-app-execute-1", "sample-app-execute-2" };
    }

}
