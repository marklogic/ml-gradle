package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import org.junit.After;
import org.junit.Test;

import java.io.File;

public class DeployDatabasesInOrderTest extends AbstractAppDeployerTest {

	@After
	public void teardown() {
		undeploySampleApp();

		DatabaseManager mgr = new DatabaseManager(manageClient);
		assertFalse(mgr.exists("sample-app-A"));
		assertFalse(mgr.exists("sample-app-B"));
		assertFalse(mgr.exists("sample-app-C"));
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
