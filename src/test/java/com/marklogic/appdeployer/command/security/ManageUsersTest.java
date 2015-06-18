package com.marklogic.appdeployer.command.security;

import org.junit.Before;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.rest.mgmt.ResourceManager;
import com.marklogic.rest.mgmt.security.UserManager;

public class ManageUsersTest extends AbstractManageResourceTest {

    @Before
    public void setup() {
        initializeAppDeployer(new CreateUsersCommand());
    }

    @Override
    protected ResourceManager newResourceManager() {
        return new UserManager(manageClient);
    }

    @Override
    protected String[] getResourceNames() {
        return new String[] { "sample-app-jane", "sample-app-john" };
    }
}
