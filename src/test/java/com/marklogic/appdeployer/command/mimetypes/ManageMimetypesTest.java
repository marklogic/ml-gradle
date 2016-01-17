package com.marklogic.appdeployer.command.mimetypes;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.mgmt.ResourceManager;
import com.marklogic.mgmt.mimetypes.MimetypeManager;

public class ManageMimetypesTest extends AbstractManageResourceTest {

    @Override
    protected ResourceManager newResourceManager() {
        return new MimetypeManager(manageClient);
    }

    @Override
    protected Command newCommand() {
        return new DeployMimetypesCommand();
    }

    @Override
    protected String[] getResourceNames() {
        return new String[] { "text/gradle" };
    }

}
