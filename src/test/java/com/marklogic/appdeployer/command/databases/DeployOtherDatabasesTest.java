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
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class DeployOtherDatabasesTest extends AbstractAppDeployerTest {

	@Test
	public void dontCreateForests() {
		ConfigDir configDir = appConfig.getFirstConfigDir();
		configDir.setBaseDir(new File("src/test/resources/sample-app/lots-of-databases"));

		appConfig.setResourceFilenamesIncludePattern(Pattern.compile("other-schemas-database.*"));
		appConfig.setCreateForests(false);

		initializeAppDeployer(new DeployOtherDatabasesCommand());

		final String dbName = "other-sample-app-schemas";
		DatabaseManager dbMgr = new DatabaseManager(manageClient);

		try {
			appDeployer.deploy(appConfig);
			assertTrue(dbMgr.exists(dbName));
			assertTrue(dbMgr.getForestIds(dbName).isEmpty(), "No forests should have been created for the database");
		} finally {
			undeploySampleApp();
			assertFalse(dbMgr.exists(dbName));
		}
	}

	@Test
	public void test() {
		ConfigDir configDir = appConfig.getFirstConfigDir();
		configDir.setBaseDir(new File("src/test/resources/sample-app/lots-of-databases"));

		appConfig.setContentForestsPerHost(2);
		appConfig.getForestCounts().put("other-sample-app-content", 2);
		appConfig.getForestCounts().put("other-sample-app-schemas", 3);
		appConfig.setResourceFilenamesToIgnore("ignored-database.json");

		// Speed up this test and ensure that all the forests still get created correctly
		appConfig.getCmaConfig().setDeployForests(true);

		initializeAppDeployer(new DeployOtherDatabasesCommand());

		DatabaseManager dbMgr = new DatabaseManager(manageClient);

		String[] dbNames = new String[]{"sample-app-content", "sample-app-triggers", "sample-app-schemas",
			"other-sample-app-content", "other-sample-app-triggers", "other-sample-app-schemas"};
		try {
			appDeployer.deploy(appConfig);

			for (String name : dbNames) {
				assertTrue(dbMgr.exists(name), "Expected to find database: " + name);
			}
			assertFalse(dbMgr.exists("ignored-content"), "ignored-database.json should have been ignored");

			assertEquals(2, dbMgr.getForestIds("sample-app-content").size(),
				"The main content database should have 2 forests, as set in the command");
			assertEquals(2, dbMgr.getForestIds("other-sample-app-content").size(),
				"AppConfig is configured for other-sample-app-content to have 2 forests instead of 1");
			assertEquals(3, dbMgr.getForestIds("other-sample-app-schemas").size(),
				"other-sample-app-schemas is configured to have 3 forests");
			assertEquals(1, dbMgr.getForestIds("other-sample-app-triggers").size(),
				"other-sample-app-triggers should have the default of 1 forest");
		} finally {
			undeploySampleApp();

			for (String name : dbNames) {
				assertFalse(dbMgr.exists(name), "Expected to not find database: " + name);
			}
		}
	}
}
