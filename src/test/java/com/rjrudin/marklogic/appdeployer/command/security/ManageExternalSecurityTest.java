package com.rjrudin.marklogic.appdeployer.command.security;

import com.rjrudin.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.rjrudin.marklogic.appdeployer.command.Command;
import com.rjrudin.marklogic.appdeployer.command.security.CreateExternalSecurityCommand;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.security.ExternalSecurityManager;

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
