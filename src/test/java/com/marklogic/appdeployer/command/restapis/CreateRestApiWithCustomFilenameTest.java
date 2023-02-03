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
