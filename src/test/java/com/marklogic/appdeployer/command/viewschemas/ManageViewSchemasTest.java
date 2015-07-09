package com.marklogic.appdeployer.command.viewschemas;

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.appservers.ManageOtherServersCommand;
import com.marklogic.appdeployer.command.databases.CreateSchemasDatabaseCommand;
import com.marklogic.appdeployer.command.databases.CreateTriggersDatabaseCommand;
import com.marklogic.appdeployer.command.databases.UpdateContentDatabasesCommand;
import com.marklogic.appdeployer.command.restapis.CreateRestApiServersCommand;
import com.marklogic.rest.mgmt.ResourceManager;
import com.marklogic.rest.mgmt.viewschemas.ViewSchemaManager;

public class ManageViewSchemasTest extends AbstractManageResourceTest {

    @Override
    protected void initializeAndDeploy() {
        initializeAppDeployer(new CreateRestApiServersCommand(), new CreateSchemasDatabaseCommand(),
                new ManageOtherServersCommand(), new CreateTriggersDatabaseCommand(),
                new UpdateContentDatabasesCommand(), newCommand());

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

    /**
     * We don't need to verify anything here, as SQL schemas and views live in the sample-app-schemas database which was
     * already deleted.
     */
    @Override
    protected void verifyResourcesWereDeleted(ResourceManager mgr) {
    }

}
