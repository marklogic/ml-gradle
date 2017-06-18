package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.ExternalSecurityManager;

public class ManageExternalSecurityTest extends AbstractManageResourceTest {

    @Override
    protected ResourceManager newResourceManager() {
        return new ExternalSecurityManager(manageClient);
    }

    @Override
    protected Command newCommand() {
        return new DeployExternalSecurityCommand();
    }

    @Override
    protected String[] getResourceNames() {
        return new String[] { "sample-app-external-security" };
    }

}
