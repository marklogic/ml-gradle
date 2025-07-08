/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.restapis;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.resource.forests.ForestManager;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateRestApiWithCustomFilenameTest extends AbstractAppDeployerTest {

    @Test
    public void customFilename() {
        appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/rest-api-custom-filename")));

        initializeAppDeployer(new DeployRestApiServersCommand("my-custom-rest-api.json", true));

        ServerManager serverMgr = new ServerManager(manageClient);
        DatabaseManager dbMgr = new DatabaseManager(manageClient);
        ForestManager forestMgr = new ForestManager(manageClient);

        try {
            appDeployer.deploy(appConfig);

            assertTrue(serverMgr.exists("my-custom-rest-api"));
            assertTrue(dbMgr.exists("my-custom-content"));
            assertTrue(dbMgr.exists("my-custom-modules"));
            assertTrue(forestMgr.exists("my-custom-content-1"));
            assertFalse(forestMgr.exists("my-custom-content-2"), "The custom REST API file only asks for 1 forest");
        } finally {
            undeploySampleApp();

            assertFalse(serverMgr.exists("my-custom-rest-api"));
            assertFalse(dbMgr.exists("my-custom-content"));
            assertFalse(dbMgr.exists("my-custom-modules"));
            assertFalse(forestMgr.exists("my-custom-content-1"));
        }
    }
}
