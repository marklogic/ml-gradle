package com.marklogic.appdeployer.mgmt;

import org.junit.Test;

import com.marklogic.appdeployer.mgmt.services.ServiceManager;

public class DeleteRestApiTest extends AbstractMgmtTest {

    @Test
    public void createAndDelete() {
        ServiceManager mgr = new ServiceManager(manageClient);

        createSampleApp();
        assertTrue("The REST API server should exist", mgr.restApiServerExists(SAMPLE_APP_NAME));

        deleteSampleApp();
        assertFalse("The REST API server should have been deleted", mgr.restApiServerExists(SAMPLE_APP_NAME));
    }
}
