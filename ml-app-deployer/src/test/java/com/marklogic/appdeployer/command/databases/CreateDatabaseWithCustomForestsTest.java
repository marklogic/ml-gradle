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
package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.resource.forests.ForestManager;
import com.marklogic.rest.util.Fragment;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The REST API command can be used to create a server with a content database, but that doesn't give any control over
 * the details of the forests. DeployForestsCommand can be used for that kind of control.
 */
public class CreateDatabaseWithCustomForestsTest extends AbstractAppDeployerTest {

    @Test
    public void contentDatabaseWithNoForestFile() {
        // We want both main and test databases
        appConfig.setTestRestPort(SAMPLE_APP_TEST_REST_PORT);

        final int numberOfForests = 4;

        DeployOtherDatabasesCommand command = new DeployOtherDatabasesCommand();
        command.setForestsPerHost(numberOfForests);
        command.setForestFilename(null);

        initializeAppDeployer(command);

        ForestManager forestMgr = new ForestManager(manageClient);
        DatabaseManager dbMgr = new DatabaseManager(manageClient);

        try {
            appDeployer.deploy(appConfig);

            assertTrue(dbMgr.exists(appConfig.getContentDatabaseName()));
            assertTrue(dbMgr.exists(appConfig.getTestContentDatabaseName()));
            assertTrue(dbMgr.exists(appConfig.getTriggersDatabaseName()));
            assertTrue(dbMgr.exists(appConfig.getSchemasDatabaseName()));

            Fragment mainDb = dbMgr.getAsXml(appConfig.getContentDatabaseName());
            Fragment testDb = dbMgr.getAsXml(appConfig.getTestContentDatabaseName());

            // Assert that the content forests and test content forests were all created
            for (int i = 1; i <= numberOfForests; i++) {
                String mainForestName = appConfig.getContentDatabaseName() + "-" + i;
                assertTrue(forestMgr.exists(mainForestName));
                assertTrue(mainDb.elementExists(format("//db:relation[db:nameref = '%s']", mainForestName)));

                String testForestName = appConfig.getTestContentDatabaseName() + "-" + i;
                assertTrue(forestMgr.exists(testForestName));
                assertTrue(testDb.elementExists(format("//db:relation[db:nameref = '%s']", testForestName)));
            }

        } finally {
            undeploySampleApp();

            assertFalse(dbMgr.exists(appConfig.getContentDatabaseName()));
            assertFalse(dbMgr.exists(appConfig.getTestContentDatabaseName()));
            assertFalse(dbMgr.exists(appConfig.getTriggersDatabaseName()));
            assertFalse(dbMgr.exists(appConfig.getSchemasDatabaseName()));

            for (int i = 1; i <= numberOfForests; i++) {
                assertFalse(forestMgr.exists(appConfig.getContentDatabaseName() + "-1"));
                assertFalse(forestMgr.exists(appConfig.getTestContentDatabaseName() + "-1"));
            }
        }
    }

    @Test
    public void configDirWithDatabaseFileButNoForestFile() {
        appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/db-only-config"));

        final int numberOfForests = 2;

        initializeAppDeployer(new DeployOtherDatabasesCommand(numberOfForests));

        ForestManager forestMgr = new ForestManager(manageClient);
        DatabaseManager dbMgr = new DatabaseManager(manageClient);

        try {
            appDeployer.deploy(appConfig);

            assertTrue(dbMgr.exists(appConfig.getContentDatabaseName()));

            Fragment mainDb = dbMgr.getAsXml(appConfig.getContentDatabaseName());

            for (int i = 1; i <= numberOfForests; i++) {
                String mainForestName = appConfig.getContentDatabaseName() + "-" + i;
                assertTrue(forestMgr.exists(mainForestName));
                assertTrue(mainDb.elementExists(format("//db:relation[db:nameref = '%s']", mainForestName)));
            }
        } finally {
            undeploySampleApp();

            assertFalse(dbMgr.exists(appConfig.getContentDatabaseName()));

            for (int i = 1; i <= numberOfForests; i++) {
                assertFalse(forestMgr.exists(appConfig.getContentDatabaseName() + "-1"));
            }
        }
    }
}
