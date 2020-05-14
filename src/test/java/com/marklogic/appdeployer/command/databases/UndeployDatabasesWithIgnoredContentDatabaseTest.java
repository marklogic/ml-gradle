package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import org.junit.Test;

import java.io.File;

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
			assertTrue(
				"The database should still exist since the database file was ignored",
				mgr.exists(dbName)
			);
		} finally {
			mgr.deleteByName(appConfig.getContentDatabaseName());
			assertFalse("Verifying the database was deleted", mgr.exists(dbName));
		}
	}
}
