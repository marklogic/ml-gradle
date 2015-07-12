package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.rest.mgmt.ResourceManager;
import com.marklogic.rest.mgmt.security.ExternalSecurityManager;

public class ManageExternalSecurityTest extends AbstractManageResourceTest {

    @Override
    protected ResourceManager newResourceManager() {
        return new ExternalSecurityManager(manageClient);
    }

    @Override
    protected Command newCommand() {
        return new CreateExternalSecurityCommand();
    }

    @Override
    protected String[] getResourceNames() {
        return new String[] { "sample-app-external-security" };
    }

}
