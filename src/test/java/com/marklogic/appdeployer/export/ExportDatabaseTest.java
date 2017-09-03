package com.marklogic.appdeployer.export;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.forests.DeployCustomForestsCommand;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.resource.forests.ForestManager;
import org.junit.After;
import org.junit.Test;

import java.io.File;

public class ExportDatabaseTest extends AbstractExportTest {

	@After
	public void teardown() {
		// No matter what happens, try to undeploy the sample app
		undeploySampleApp();
	}

	@Test
	public void exportDatabaseWithTwoForests() throws Exception {
		// Deploy our simple app
		appConfig.getConfigDir().setBaseDir(new File("src/test/resources/sample-app/db-only-config"));
		initializeAppDeployer(new DeployContentDatabasesCommand(2));
		deploySampleApp();

		// Export the database, which by default will export the forests
		new Exporter(manageClient).databases(appConfig.getContentDatabaseName()).export(exportDir);

		// Verify the database file
		File dbFile = new File(exportDir, "databases/sample-app-content.json");
		assertTrue(dbFile.exists());
		ObjectNode dbNode = (ObjectNode) objectMapper.readTree(dbFile);
		assertFalse("The forest key should have been removed so the database can be created before the forests exist",
			dbNode.has("forest"));

		// Verify a forest file
		File forest1File = new File(exportDir, "forests/sample-app-content/sample-app-content-1.json");
		assertTrue(forest1File.exists());
		ObjectNode forestNode = (ObjectNode) objectMapper.readTree(forest1File);
		assertFalse("The range key should have been removed due to a bug in the Manage API, where range:null causes an error",
			forestNode.has("range"));

		// Undeploy the app so we can try to deploy it from the export dir
		undeploySampleApp();

		// Deploy from the export dir!
		appConfig.getConfigDir().setBaseDir(exportDir);
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
