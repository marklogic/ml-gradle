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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Doesn't actually use a command, but it's nice to extend the parent test class.
 */
public class SetUpdatesAllowedOnDatabaseForestsTest extends AbstractAppDeployerTest {

    @AfterEach
    public void teardown() {
        undeploySampleApp();
    }

    @Test
    public void test() {
        appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/db-only-config"));
        initializeAppDeployer(new DeployOtherDatabasesCommand(2));
        appDeployer.deploy(appConfig);

        DatabaseManager dbMgr = new DatabaseManager(this.manageClient);
        dbMgr.setUpdatesAllowedOnPrimaryForests(appConfig.getContentDatabaseName(), "flash-backup");

        try {
	        ForestManager forestMgr = new ForestManager(this.manageClient);
	        assertEquals("flash-backup", forestMgr.getPropertiesAsXml("sample-app-content-1").getElementValue("//m:updates-allowed"));
	        assertEquals("flash-backup", forestMgr.getPropertiesAsXml("sample-app-content-2").getElementValue("//m:updates-allowed"));
        } finally {
	        // Gotta set it back to all so the database/forests can be deleted
	        dbMgr.setUpdatesAllowedOnPrimaryForests(appConfig.getContentDatabaseName(), "all");
        }
    }
}
