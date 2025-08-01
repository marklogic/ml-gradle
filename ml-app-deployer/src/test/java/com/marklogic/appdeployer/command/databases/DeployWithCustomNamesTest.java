/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.resource.forests.ForestManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Verifies that all the default names in AppConfig can be overridden successfully.
 */
public class DeployWithCustomNamesTest extends AbstractAppDeployerTest {

    private DatabaseManager dbMgr;
    private ForestManager forestMgr;
    private ServerManager serverMgr;

    private String prefix = "custom-sample-app";

    @Test
    public void test() {
        appConfig.setTestRestPort(SAMPLE_APP_TEST_REST_PORT);

        appConfig.setRestServerName(prefix);
        appConfig.setTestRestServerName(prefix + "-test");
        appConfig.setContentDatabaseName(prefix + "-content");
        appConfig.setTestContentDatabaseName(prefix + "-test-content");
        appConfig.setModulesDatabaseName(prefix + "-modules");
        appConfig.setTriggersDatabaseName(prefix + "-triggers");
        appConfig.setSchemasDatabaseName(prefix + "-schemas");
        appConfig.setContentForestsPerHost(2);

        initializeAppDeployer(new DeployRestApiServersCommand(), new DeployOtherDatabasesCommand(1));

        dbMgr = new DatabaseManager(manageClient);
        forestMgr = new ForestManager(manageClient);
        serverMgr = new ServerManager(manageClient, "Default");

        try {
            appDeployer.deploy(appConfig);
            assertResourcesExist(true);
        } finally {
            undeploySampleApp();
            assertResourcesExist(false);
        }
    }

    private void assertResourcesExist(boolean exists) {
        assertEquals(exists, dbMgr.exists(prefix + "-content"));
        assertEquals(exists, dbMgr.exists(prefix + "-test-content"));
        assertEquals(exists, dbMgr.exists(prefix + "-triggers"));
        assertEquals(exists, dbMgr.exists(prefix + "-schemas"));
        assertEquals(exists, dbMgr.exists(prefix + "-modules"));

        // Assumes two content forests
        assertEquals(exists, forestMgr.exists(prefix + "-content-1"));
        assertEquals(exists, forestMgr.exists(prefix + "-content-2"));
        assertFalse(forestMgr.exists(prefix + "-content-3"));

        // Assumes two test-content forests
        assertEquals(exists, forestMgr.exists(prefix + "-test-content-1"));
        assertEquals(exists, forestMgr.exists(prefix + "-test-content-2"));
        assertFalse(forestMgr.exists(prefix + "-test-content-3"));

        // Assumes one forest
        assertEquals(exists, forestMgr.exists(prefix + "-triggers-1"));
        assertFalse(forestMgr.exists(prefix + "-triggers-2"));

        // Assumes one forest
        assertEquals(exists, forestMgr.exists(prefix + "-schemas-1"));
        assertFalse(forestMgr.exists(prefix + "-schemas-2"));

        // Assumes one forest
        assertEquals(exists, forestMgr.exists(prefix + "-modules-1"));
        assertFalse(forestMgr.exists(prefix + "-modules-2"));

        assertEquals(exists, serverMgr.exists(prefix));
        assertEquals(exists, serverMgr.exists(prefix + "-test"));
    }
}
