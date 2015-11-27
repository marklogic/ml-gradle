package com.rjrudin.marklogic.appdeployer.command.triggers;

import com.rjrudin.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.rjrudin.marklogic.appdeployer.command.Command;
import com.rjrudin.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand;
import com.rjrudin.marklogic.appdeployer.command.databases.DeploySchemasDatabaseCommand;
import com.rjrudin.marklogic.appdeployer.command.databases.DeployTriggersDatabaseCommand;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.triggers.TriggerManager;

public class ManageTriggersTest extends AbstractManageResourceTest {

    @Override
    protected void initializeAndDeploy() {
        initializeAppDeployer(new DeploySchemasDatabaseCommand(), new DeployTriggersDatabaseCommand(),
                new DeployContentDatabasesCommand(), newCommand());
        appDeployer.deploy(appConfig);
    }

    @Override
    protected ResourceManager newResourceManager() {
        return new TriggerManager(manageClient, appConfig.getTriggersDatabaseName());
    }

    @Override
    protected Command newCommand() {
        return new DeployTriggersCommand();
    }

    @Override
    protected String[] getResourceNames() {
        return new String[] { "my-trigger" };
    }

    /**
     * No need to do anything here, as the triggers are deleted when the triggers database is deleted.
     */
    @Override
    protected void verifyResourcesWereDeleted(ResourceManager mgr) {
    }
}
