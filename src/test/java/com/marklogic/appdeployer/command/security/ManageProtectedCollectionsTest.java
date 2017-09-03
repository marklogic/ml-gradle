package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.ProtectedCollectionsManager;

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
