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
package com.marklogic.appdeployer.command;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.resource.ResourceManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Provides a basic create-then-delete test for testing any implementation of ResourceManager.
 */
public abstract class AbstractManageResourceTest extends AbstractAppDeployerTest {

    /**
     * @return an instance of the ResourceManager that supports the resource being tested
     */
    protected abstract ResourceManager newResourceManager();

    /**
     * @return an instance of the Command class that is being tested
     */
    protected abstract Command newCommand();

    /**
     * @return an array of resource names that can be used to verify that each resource was created successfully. Each
     *         resource name should typically correspond to a test file under
     *         src/test/resources/sample-app/src/main/ml-config in the directory that corresponds to the resource being
     *         tested.
     */
    protected abstract String[] getResourceNames();

    /**
     * A subclass can override this to perform additional assertions on any resources that were created before the
     * resources are deleted.
     */
    protected void afterResourcesCreated() {
    }

    /**
     * A subclass can override this to perform additional assertions after the second deploy call has been made, which
     * most often will result in the resource being updated.
     */
    protected void afterResourcesCreatedAgain() {

    }

    /**
     * Performs a generic test of creating a resource, then creating it again (to ensure that doesn't cause any failures
     * - this should typically result in an update instead of a create), and then deleting it.
     */
    @Test
    public void createThenDelete() {
        ResourceManager mgr = newResourceManager();

        initializeAndDeploy();

        try {
            for (String name : getResourceNames()) {
                assertTrue(mgr.exists(name));
            }

            // Let the subclass optionally perform any additional assertions
            afterResourcesCreated();

            // Make sure we don't get an error from trying to create the resources again
            appDeployer.deploy(appConfig);

            afterResourcesCreatedAgain();
        } finally {
            undeployAndVerifyResourcesWereDeleted(mgr);
        }
    }

    protected void initializeAndDeploy() {
        initializeAppDeployer(newCommand());
        appDeployer.deploy(appConfig);
    }

    protected void undeployAndVerifyResourcesWereDeleted(ResourceManager mgr) {
        appDeployer.undeploy(appConfig);
        verifyResourcesWereDeleted(mgr);
    }

    protected void verifyResourcesWereDeleted(ResourceManager mgr) {
        for (String name : getResourceNames()) {
            assertFalse(mgr.exists(name));
        }
    }
}
