package com.marklogic.appdeployer.command.security;

import org.junit.Before;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.rest.mgmt.ResourceManager;
import com.marklogic.rest.mgmt.security.UserManager;
import com.marklogic.rest.util.Fragment;

public class ManageUsersTest extends AbstractManageResourceTest {

    @Before
    public void setup() {
        appConfig.getCustomTokens().put("CUSTOM_TOKEN_FOR_JOHN_DESCRIPTION", "This was set via a custom token");
    }

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

    @Override
    protected void afterResourcesCreated() {
        UserManager mgr = new UserManager(manageClient);
        Fragment f = mgr.getAsXml("sample-app-john");
        assertEquals("This was set via a custom token", f.getElementValue("/msec:user-default/msec:description"));
    }
}
