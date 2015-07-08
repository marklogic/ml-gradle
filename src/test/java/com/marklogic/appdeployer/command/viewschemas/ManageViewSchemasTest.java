package com.marklogic.appdeployer.command.viewschemas;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.rest.mgmt.ResourceManager;
import com.marklogic.rest.mgmt.tasks.ViewSchemaManager;

public class ManageViewSchemasTest extends AbstractManageResourceTest {

    @Override
    protected ResourceManager newResourceManager() {
        return new ViewSchemaManager(manageClient, appConfig.getContentDatabaseName());
    }

    @Override
    protected Command newCommand() {
        return new ManageViewSchemasCommand();
    }

    @Override
    protected String[] getResourceNames() {
        return new String[] { "main" };
    }

}
