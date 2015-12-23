package com.marklogic.appdeployer.command.servers;

import org.junit.After;
import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.appservers.DeployOtherServersCommand;
import com.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand;
import com.marklogic.appdeployer.command.databases.DeploySchemasDatabaseCommand;
import com.marklogic.appdeployer.command.databases.DeployTriggersDatabaseCommand;

/**
 * Only purpose of this test so far is to ensure that we wait properly after deleting the server.
 */
public class ManageOtherServerTest extends AbstractAppDeployerTest {

    @After
    public void teardown() {
        undeploySampleApp();
    }

    @Test
    public void updateMainAndRestRestApiServers() {
        // Create some other databases that have to be deleted to ensure we wait for a restart after deleting the ODBC
        // server
        initializeAppDeployer(new DeployContentDatabasesCommand(1), new DeployTriggersDatabaseCommand(),
                new DeploySchemasDatabaseCommand(), new DeployOtherServersCommand());
        appConfig.getCustomTokens().put("%%ODBC_PORT%%", "8048");
        appDeployer.deploy(appConfig);

    }
}
