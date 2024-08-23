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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.appservers.UpdateRestApiServersCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import com.marklogic.rest.util.Fragment;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UpdateRestApiServersTest extends AbstractAppDeployerTest {

    @AfterEach
    public void teardown() {
        undeploySampleApp();
    }

    @Test
    public void updateMainAndRestRestApiServers() {
        // Deploy a REST API server and a test one too
	    initializeAppDeployer(new DeployRestApiServersCommand(true));

        appConfig.setTestRestPort(SAMPLE_APP_TEST_REST_PORT);
        appDeployer.deploy(appConfig);

        assertAuthentication("The REST API server auth should default to digest", appConfig.getRestServerName(),
                "digest");
        assertAuthentication("The test REST API server auth should default to digest",
                appConfig.getTestRestServerName(), "digest");

        // Now redeploy with the update command
        initializeAppDeployer(new DeployRestApiServersCommand(true), new UpdateRestApiServersCommand());
        appDeployer.deploy(appConfig);

        assertAuthentication(
                "The REST API server auth should now be set to basic because of what's in the rest-api-server.json file",
                appConfig.getRestServerName(), "basic");
        assertAuthentication(
                "The test REST API server auth should now be set to basic because of what's in the rest-api-server.json file",
                appConfig.getTestRestServerName(), "basic");
    }

    private void assertAuthentication(String message, String serverName, String auth) {
        Fragment xml = new ServerManager(manageClient, appConfig.getGroupName()).getPropertiesAsXml(serverName);
        assertTrue(xml.elementExists(String.format("/m:http-server-properties/m:authentication[. = '%s']", auth)),
			message);
    }

}
