package com.marklogic.appdeployer.command;

import org.junit.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.rest.mgmt.ResourceManager;

/**
 * Provides a basic create-then-delete test for testing any implementation of ResourceManager.
 */
public abstract class AbstractManageResourceTest extends AbstractAppDeployerTest {

    protected abstract ResourceManager newResourceManager();

    protected abstract Command newCommand();

    /**
     * @return an array of resource names that can be used to verify that each resource was created successfully
     */
    protected abstract String[] getResourceNames();

    /**
     * A subclass can override this to perform additional assertions on any resources that were created before the
     * resources are deleted.
     */
    protected void afterResourcesCreated() {

    }

    @Test
    public void createThenDelete() {
        ResourceManager mgr = newResourceManager();

        initializeAppDeployer(newCommand());
        appDeployer.deploy(appConfig);

        try {
            for (String name : getResourceNames()) {
                assertTrue(mgr.exists(name));
            }

            // Let the subclass optionally perform any additional assertions
            afterResourcesCreated();

            // Make sure we don't get an error from trying to create the roles again
            appDeployer.deploy(appConfig);
        } finally {
            undeployAndVerifyResourcesWereDeleted(mgr);
        }
    }

    protected void undeployAndVerifyResourcesWereDeleted(ResourceManager mgr) {
        appDeployer.undeploy(appConfig);

        for (String name : getResourceNames()) {
            assertFalse(mgr.exists(name));
        }
    }
}
