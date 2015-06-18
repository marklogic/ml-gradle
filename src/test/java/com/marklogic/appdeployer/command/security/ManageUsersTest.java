package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.rest.mgmt.ResourceManager;
import com.marklogic.rest.mgmt.security.UserManager;

public class ManageUsersTest extends AbstractManageResourceTest {

    @Override
    protected ResourceManager newResourceManager() {
        return new UserManager(manageClient);
    }

    @Override
    protected Command newCommand() {
        return new CreateUsersCommand();
    }

    @Override
    protected String[] getResourceNames() {
        return new String[] { "sample-app-jane", "sample-app-john" };
    }
}
