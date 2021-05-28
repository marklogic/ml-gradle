package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeployDatabasesAndSubDatabasesTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		ConfigDir configDir = appConfig.getFirstConfigDir();
		configDir.setBaseDir(new File("src/test/resources/sample-app/subdatabases"));

		initializeAppDeployer(new DeployOtherDatabasesCommand(1));

		DatabaseManager dbMgr = new DatabaseManager(manageClient);

		String[] dbNames = new String[]{"sample-app-content", "sample-app-triggers", "sample-app-schemas",
			"mysuperdb", "mysuperdb-subdb01", "mysuperdb-subdb02", "sample-app-content-subdb01", "sample-app-content-subdb01"};

		try {
			appDeployer.deploy(appConfig);

			for (String name : dbNames) {
				assertTrue(dbMgr.exists(name), "Expected to find database: " + name);
			}

			// check that subdatabases are associated
			List<String> subDatabases = dbMgr.getSubDatabases("mysuperdb");
			assertTrue(subDatabases.contains("mysuperdb-subdb01"), "Expected to find subdatabase of 'mysuperdb-subdb01'");
			assertTrue(subDatabases.contains("mysuperdb-subdb02"), "Expected to find subdatabase of 'mysuperdb-subdb02'");

			subDatabases = dbMgr.getSubDatabases("sample-app-content");
			assertTrue(subDatabases.contains("sample-app-content-subdb01"), "Expected to find subdatabase of 'sample-app-content-subdb01'");
			assertTrue(subDatabases.contains("sample-app-content-subdb02"), "Expected to find subdatabase of 'sample-app-content-subdb02'");


		} finally {
			undeploySampleApp();
			for (String name : dbNames) {
				assertFalse(dbMgr.exists(name), "Expected to not find database: " + name);
			}
		}
	}

}
