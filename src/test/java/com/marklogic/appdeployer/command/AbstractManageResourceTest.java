package com.marklogic.appdeployer.command;

import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.rest.mgmt.ResourceManager;

/**
 * Provides a basic create-then-delete test for testing any implementation of ResourceManager.
 */
public abstract class AbstractManageResourceTest extends AbstractAppDeployerTest {

    protected abstract ResourceManager newResourceManager();

    protected abstract String[] getResourceNames();

    @Test
    public void createThenDelete() {
        ResourceManager mgr = newResourceManager();

        appDeployer.deploy(appConfig);

        for (String name : getResourceNames()) {
            assertTrue(mgr.exists(name));
        }

        try {
            // Make sure we don't get an error from trying to create the roles again
            appDeployer.deploy(appConfig);
        } finally {
            // Now undo
            appDeployer.undeploy(appConfig);

            for (String name : getResourceNames()) {
                assertFalse(mgr.exists(name));
            }
        }
    }
}
