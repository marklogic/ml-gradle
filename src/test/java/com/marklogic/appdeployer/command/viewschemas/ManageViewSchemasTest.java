package com.marklogic.appdeployer.command.viewschemas;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.appservers.ManageOtherServersCommand;
import com.marklogic.appdeployer.command.restapis.CreateRestApiServersCommand;
import com.marklogic.rest.mgmt.ResourceManager;
import com.marklogic.rest.mgmt.viewschemas.ViewSchemaManager;

public class ManageViewSchemasTest extends AbstractManageResourceTest {

    @Override
    protected void initializeAndDeploy() {
        initializeAppDeployer(new CreateRestApiServersCommand(), newCommand(), new ManageOtherServersCommand());
        appConfig.getCustomTokens().put("%%ODBC_PORT%%", "8542");
        appDeployer.deploy(appConfig);
    }

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

    @Override
    protected void undeployAndVerifyResourcesWereDeleted(ResourceManager mgr) {
        // Nothing to do here, as the content database is deleted which holds all the view schemas
    }

}
