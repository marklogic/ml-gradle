package com.marklogic.appdeployer.command.servers;

import java.io.File;

import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.appservers.DeployOtherServersCommand;
import com.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand;
import com.marklogic.appdeployer.command.databases.DeploySchemasDatabaseCommand;
import com.marklogic.appdeployer.command.databases.DeployTriggersDatabaseCommand;
import com.marklogic.mgmt.appservers.ServerManager;

/**
 * Main purpose of these tests is to ensure that we wait properly after deleting a server.
 */
public class ManageOtherServerTest extends AbstractAppDeployerTest {

    @Test
    public void updateMainAndRestRestApiServers() {
        // Create some other databases that have to be deleted to ensure we wait for a restart after deleting the ODBC
        // server
        initializeAppDeployer(new DeployContentDatabasesCommand(1), new DeployTriggersDatabaseCommand(),
                new DeploySchemasDatabaseCommand(), new DeployOtherServersCommand());
        appConfig.getCustomTokens().put("%%ODBC_PORT%%", "8048");
        try {
            appDeployer.deploy(appConfig);
        } finally {
            appDeployer.undeploy(appConfig);
        }
    }

    @Test
    public void odbcAndXdbcServers() {
        appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/other-servers")));

        ServerManager mgr = new ServerManager(manageClient);

        initializeAppDeployer(new DeployOtherServersCommand());
        appConfig.getCustomTokens().put("%%ODBC_PORT%%", "8048");
        appConfig.getCustomTokens().put("%%XDBC_PORT%%", "8049");
        appDeployer.deploy(appConfig);

        assertTrue(mgr.exists("sample-app-xdbc"));
        assertTrue(mgr.exists("sample-app-odbc"));
        
        appDeployer.undeploy(appConfig);

        assertFalse(mgr.exists("sample-app-xdbc"));
        assertFalse(mgr.exists("sample-app-odbc"));
    }
}
