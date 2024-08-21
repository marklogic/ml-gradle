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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies support for bumping up the number of content forests and then re-deploying. Does not yet support lowering
 * the number of content forests and expecting the existing ones to be detached/deleted.
 */
public class UpdateForestCountTest extends AbstractAppDeployerTest {

    @Test
    public void test() {
        appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/db-only-config"));
        DatabaseManager mgr = new DatabaseManager(manageClient);

        initializeAppDeployer(new DeployOtherDatabasesCommand());

        appConfig.setContentForestsPerHost(1);
        appDeployer.deploy(appConfig);
        assertEquals(1, mgr.getForestIds(appConfig.getContentDatabaseName()).size(), "Should only have 1 forest");

        appConfig.setContentForestsPerHost(2);
        appDeployer.deploy(appConfig);
        assertEquals(2, mgr.getForestIds(appConfig.getContentDatabaseName()).size(), "Should now have 2 forests");

        appDeployer.deploy(appConfig);
        assertEquals(2, mgr.getForestIds(appConfig.getContentDatabaseName()).size(), "Should still have 2 forests");

        appConfig.setContentForestsPerHost(1);
        appDeployer.deploy(appConfig);
        assertEquals(2, mgr.getForestIds(appConfig.getContentDatabaseName()).size(),
			"Should still have 2 forests, we don't yet support deleting forests when the number drops");
    }

    @AfterEach
    public void teardown() {
        undeploySampleApp();
    }
}
