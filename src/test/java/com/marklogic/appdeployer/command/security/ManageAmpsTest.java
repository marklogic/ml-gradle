package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.mgmt.ResourceManager;
import com.marklogic.mgmt.security.AmpManager;

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
