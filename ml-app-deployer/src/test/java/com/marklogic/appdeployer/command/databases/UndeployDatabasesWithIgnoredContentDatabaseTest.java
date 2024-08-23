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
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UndeployDatabasesWithIgnoredContentDatabaseTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		initializeAppDeployer(new DeployOtherDatabasesCommand(1));

		// Deploy the database
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/db-only-config"));
		deploySampleApp();

		// Ignore the file
		appConfig.setResourceFilenamesToIgnore("content-database.json");
		undeploySampleApp();

		DatabaseManager mgr = new DatabaseManager(manageClient);
		String dbName = appConfig.getContentDatabaseName();
		try {
			// Verify db is still there
			assertTrue(mgr.exists(dbName), "The database should still exist since the database file was ignored");
		} finally {
			mgr.deleteByName(appConfig.getContentDatabaseName());
			assertFalse(mgr.exists(dbName), "Verifying the database was deleted");
		}
	}
}
