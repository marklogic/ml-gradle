/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.appdeployer.command.servers;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.appservers.DeployOtherServersCommand;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Main purpose of these tests is to ensure that we wait properly after deleting a server.
 */
public class ManageOtherServerTest extends AbstractAppDeployerTest {

    @Test
    public void updateMainAndRestRestApiServers() {
        // Create some other databases that have to be deleted to ensure we wait for a restart after deleting the ODBC
        // server
        initializeAppDeployer(new DeployOtherDatabasesCommand(1), new DeployOtherServersCommand());
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
        appDeployer.deploy(appConfig);

        assertTrue(mgr.exists("sample-app-xdbc"));
        assertTrue(mgr.exists("sample-app-odbc"));
        assertFalse(mgr.exists("sample-app"), "The command should ignore the rest-api-server.json file, as that's processed by DeployRestApiServersCommand");

        appDeployer.undeploy(appConfig);

        assertFalse(mgr.exists("sample-app-xdbc"));
        assertFalse(mgr.exists("sample-app-odbc"));
        assertFalse(mgr.exists("sample-app"));
    }

    @Test
    public void ignoreOdbcServer() {
        appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/other-servers")));

        ServerManager mgr = new ServerManager(manageClient);

        DeployOtherServersCommand c = new DeployOtherServersCommand();
        c.setFilenamesToIgnore("odbc-server.json");
        initializeAppDeployer(c);

        appDeployer.deploy(appConfig);

        final String message = "Both the ODBC and REST API server files should have been ignored";
        assertTrue(mgr.exists("sample-app-xdbc"));
        assertFalse(mgr.exists("sample-app-odbc"), message);
        assertFalse(mgr.exists("sample-app"), message);

        appDeployer.undeploy(appConfig);

        assertFalse(mgr.exists("sample-app-xdbc"));
        assertFalse(mgr.exists("sample-app-odbc"));
        assertFalse(mgr.exists("sample-app"));
    }
}
