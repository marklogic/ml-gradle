package com.rjrudin.marklogic.appdeployer.command.security;

import com.rjrudin.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.rjrudin.marklogic.appdeployer.command.Command;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.security.AmpManager;

public class ManageAmpsTest extends AbstractManageResourceTest {

    @Override
    protected ResourceManager newResourceManager() {
        return new AmpManager(manageClient);
    }

    @Override
    protected Command newCommand() {
        return new DeployAmpsCommand();
    }

    @Override
    protected String[] getResourceNames() {
        return new String[] { "sample-app-amp-1" };
    }

}
