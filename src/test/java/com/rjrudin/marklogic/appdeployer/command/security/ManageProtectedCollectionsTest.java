package com.rjrudin.marklogic.appdeployer.command.security;

import com.rjrudin.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.rjrudin.marklogic.appdeployer.command.Command;
import com.rjrudin.marklogic.appdeployer.command.security.DeployProtectedCollectionsCommand;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.security.ProtectedCollectionsManager;

public class ManageProtectedCollectionsTest extends AbstractManageResourceTest {

    @Override
    protected ResourceManager newResourceManager() {
        return new ProtectedCollectionsManager(manageClient);
    }

    @Override
    protected Command newCommand() {
        return new DeployProtectedCollectionsCommand();
    }

    @Override
    protected String[] getResourceNames() {
        return new String[] { "sample-app-collection", "http://example.org" };
    }

}
