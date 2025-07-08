/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
