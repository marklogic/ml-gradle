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
package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.mgmt.resource.forests.ForestManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that directories under ./forests/ are processed correctly.
 */
public class DeployCustomForestsTest extends AbstractAppDeployerTest {

	@AfterEach
	public void tearDown() {
		undeploySampleApp();
	}

	@Test
	public void test() {
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/custom-forests")));

		initializeAppDeployer(new DeployOtherDatabasesCommand(1), new DeployCustomForestsCommand());
		deploySampleApp();

		ForestManager mgr = new ForestManager(manageClient);
		assertFalse(mgr.exists("sample-app-content-1"), "A default forest should not have been created since custom forests exist");
		assertTrue(mgr.exists("sample-app-content-custom-1"));
		assertTrue(mgr.exists("sample-app-content-custom-2"));
		assertTrue(mgr.exists("sample-app-content-custom-3"));
	}

	@Test
	public void deployWithCma() {
		appConfig.getCmaConfig().enableAll();
		test();
	}
}
