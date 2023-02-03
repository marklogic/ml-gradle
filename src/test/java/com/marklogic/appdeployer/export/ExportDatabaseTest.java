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
package com.marklogic.appdeployer.export;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.forests.DeployCustomForestsCommand;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.resource.forests.ForestManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExportDatabaseTest extends AbstractExportTest {

	@AfterEach
	public void teardown() {
		// No matter what happens, try to undeploy the sample app
		undeploySampleApp();
	}

	@Test
	public void exportDatabaseWithTwoForests() throws Exception {
		// Deploy our simple app
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/db-only-config"));
		initializeAppDeployer(new DeployOtherDatabasesCommand(2));
		deploySampleApp();

		// Export the database, which by default will export the forests
		new Exporter(manageClient).databases(appConfig.getContentDatabaseName()).export(exportDir);

		// Verify the database file
		File dbFile = new File(exportDir, "databases/sample-app-content.json");
		assertTrue(dbFile.exists());
		ObjectNode dbNode = (ObjectNode) objectMapper.readTree(dbFile);
		assertFalse(dbNode.has("forest"),
			"The forest key should have been removed so the database can be created before the forests exist");

		// Verify a forest file
		File forest1File = new File(exportDir, "forests/sample-app-content/sample-app-content-1.json");
		assertTrue(forest1File.exists());
		ObjectNode forestNode = (ObjectNode) objectMapper.readTree(forest1File);
		assertFalse(forestNode.has("range"),
			"The range key should have been removed due to a bug in the Manage API, where range:null causes an error");

		// Undeploy the app so we can try to deploy it from the export dir
		undeploySampleApp();

		// Deploy from the export dir!
		appConfig.getFirstConfigDir().setBaseDir(exportDir);
		initializeAppDeployer(new DeployOtherDatabasesCommand(), new DeployCustomForestsCommand());
		deploySampleApp();

		// Verify the results
		DatabaseManager dbMgr = new DatabaseManager(manageClient);
		assertTrue(dbMgr.exists("sample-app-content"));
		ForestManager forestMgr = new ForestManager(manageClient);
		assertTrue(forestMgr.exists("sample-app-content-1"));
		assertTrue(forestMgr.exists("sample-app-content-2"));
	}
}
