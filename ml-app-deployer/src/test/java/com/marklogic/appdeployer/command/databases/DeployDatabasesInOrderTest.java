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
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class DeployDatabasesInOrderTest extends AbstractAppDeployerTest {

	@AfterEach
	public void teardown() {
		undeploySampleApp();

		DatabaseManager mgr = new DatabaseManager(manageClient);
		assertFalse(mgr.exists("sample-app-A"));
		assertFalse(mgr.exists("sample-app-B"));
		assertFalse(mgr.exists("sample-app-C"));
	}

	@Test
	public void test() {
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/dhf-db-ordering")));

		initializeAppDeployer(new DeployOtherDatabasesCommand());

		appConfig.getCustomTokens().put("%%mlFinalDbName%%", "sample-data-hub-FINAL");
		appConfig.getCustomTokens().put("%%mlFinalSchemasDbName%%", "sample-data-hub-final-SCHEMAS");
		appConfig.getCustomTokens().put("%%mlFinalTriggersDbName%%", "sample-data-hub-final-TRIGGERS");
		appConfig.getCustomTokens().put("%%mlModulesDbName%%", "sample-data-hub-MODULES");
		appConfig.getCustomTokens().put("%%mlTestDbName%%", "sample-data-hub-TEST");

		deploySampleApp();

		DatabaseManager mgr = new DatabaseManager(manageClient);
		assertTrue(mgr.exists("sample-data-hub-FINAL"));
		assertTrue(mgr.exists("sample-data-hub-final-SCHEMAS"));
		assertTrue(mgr.exists("sample-data-hub-final-TRIGGERS"));
		assertTrue(mgr.exists("sample-data-hub-MODULES"));
		assertTrue(mgr.exists("sample-data-hub-TEST"));

		undeploySampleApp();

		assertFalse(mgr.exists("sample-data-hub-FINAL"));
		assertFalse(mgr.exists("sample-data-hub-final-SCHEMAS"));
		assertFalse(mgr.exists("sample-data-hub-final-TRIGGERS"));
		assertFalse(mgr.exists("sample-data-hub-MODULES"));
		assertFalse(mgr.exists("sample-data-hub-TEST"));
	}

	/**
	 * Tests 3 files that have filenames that would lead them to be processed in the wrong order if the command
	 * doesn't sort them correctly.
	 */
	@Test
	public void jsonResourceFiles() {
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/databases-in-order")));
		initializeAppDeployer(new DeployOtherDatabasesCommand(1));
		deploySampleApp();

		DatabaseManager mgr = new DatabaseManager(manageClient);
		assertTrue(mgr.exists("sample-app-A"));
		assertTrue(mgr.exists("sample-app-B"));
		assertTrue(mgr.exists("sample-app-C"));
	}

	@Test
	public void xmlResourceFiles() {
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/databases-in-order-xml")));
		initializeAppDeployer(new DeployOtherDatabasesCommand(1));
		deploySampleApp();

		DatabaseManager mgr = new DatabaseManager(manageClient);
		assertTrue(mgr.exists("sample-app-A"));
		assertTrue(mgr.exists("sample-app-B"));
		assertTrue(mgr.exists("sample-app-C"));
	}

	@Test
	public void notSortingProducesFailure() {
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/databases-in-order")));
		appConfig.setSortOtherDatabaseByDependencies(false);
		initializeAppDeployer(new DeployOtherDatabasesCommand(1));

		try {
			deploySampleApp();
			fail("Deploy should have failed because the databases were not sorted based on their dependencies");
		} catch (Exception ex) {
			logger.info("Caught expected exception: " + ex.getMessage());
		}
	}
}
