package com.marklogic.appdeployer.command.servers;

import org.junit.After;
import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.restapis.CreateRestApiServersPlugin;
import com.marklogic.appdeployer.command.servers.UpdateRestApiServersPlugin;
import com.marklogic.rest.mgmt.appservers.ServerManager;
import com.marklogic.rest.util.Fragment;

public class UpdateRestApiServersTest extends AbstractAppDeployerTest {

    @After
    public void teardown() {
        undeploySampleApp();
    }

    @Test
    public void updateMainAndRestRestApiServers() {
        // Deploy a REST API server and a test one too
        initializeAppDeployer();
        appConfig.setTestRestPort(SAMPLE_APP_TEST_REST_PORT);
        appDeployer.deploy(appConfig);

        assertAuthentication("The REST API server auth should default to digest", appConfig.getRestServerName(),
                "digest");
        assertAuthentication("The test REST API server auth should default to digest",
                appConfig.getTestRestServerName(), "digest");

        // Now redeploy with the update plugin
        initializeAppDeployer(new CreateRestApiServersPlugin(), new UpdateRestApiServersPlugin());
        appDeployer.deploy(appConfig);

        assertAuthentication(
                "The REST API server auth should now be set to basic because of what's in the rest-api-server.json file",
                appConfig.getRestServerName(), "basic");
        assertAuthentication(
                "The test REST API server auth should now be set to basic because of what's in the rest-api-server.json file",
                appConfig.getTestRestServerName(), "basic");
    }

    private void assertAuthentication(String message, String serverName, String auth) {
        Fragment xml = new ServerManager(manageClient).getServerPropertiesAsXml(serverName, appConfig.getGroupName());
        assertTrue(message,
                xml.elementExists(String.format("/m:http-server-properties/m:authentication[. = '%s']", auth)));
    }

}
