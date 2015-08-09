package com.rjrudin.marklogic.appdeployer.command.restapis;

import org.junit.Before;
import org.junit.Test;

import com.rjrudin.marklogic.appdeployer.AbstractAppDeployerTest;
import com.rjrudin.marklogic.mgmt.appservers.ServerManager;
import com.rjrudin.marklogic.mgmt.restapis.RestApiManager;

/**
 * This test ensures that the convenience methods for creating and deleting a sample application work properly, and thus
 * they can be used in other tests that depend on having an app in place.
 */
public class DeleteRestApiTest extends AbstractAppDeployerTest {

    @Before
    public void setup() {
        initializeAppDeployer();
        appConfig.setTestRestPort(8541);
    }

    @Test
    public void createAndDelete() {
        RestApiManager mgr = new RestApiManager(manageClient);
        ServerManager serverMgr = new ServerManager(manageClient, appConfig.getGroupName());

        deployRestApi();
        assertTrue("The REST API server should exist", mgr.restApiServerExists(SAMPLE_APP_NAME));
        assertTrue("The REST API app server should exist", serverMgr.exists(SAMPLE_APP_NAME));

        undeploySampleApp();
        assertFalse("The REST API server should have been deleted", mgr.restApiServerExists(SAMPLE_APP_NAME));
        assertFalse("The REST API app server have been deleted", serverMgr.exists(SAMPLE_APP_NAME));
    }
}
