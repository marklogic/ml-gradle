package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import org.junit.Test;

import java.io.File;
import java.util.Set;

public class DontUndeployDefaultDatabasesTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		DatabaseManager mgr = new DatabaseManager(manageClient);

		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/default-databases")));
		initializeAppDeployer(new DeployContentDatabasesCommand(1), new DeployOtherDatabasesCommand());

		deploySampleApp();

		assertTrue(mgr.exists(appConfig.getContentDatabaseName()));
		assertTrue(mgr.exists("Fab"));

		undeploySampleApp();

		assertFalse(mgr.exists(appConfig.getContentDatabaseName()));
		assertTrue(mgr.exists("Fab"));
	}

	@Test
	public void verifyDatabasesToNotUndeploy() {
		DeployOtherDatabasesCommand c = new DeployOtherDatabasesCommand();
		Set<String> set = c.getDefaultDatabasesToNotUndeploy();
		assertTrue(set.contains("App-Services"));
		assertTrue(set.contains("Documents"));
		assertTrue(set.contains("Extensions"));
		assertTrue(set.contains("Fab"));
		assertTrue(set.contains("Last-Login"));
		assertTrue(set.contains("Meters"));
		assertTrue(set.contains("Modules"));
		assertTrue(set.contains("Schemas"));
		assertTrue(set.contains("Security"));
		assertTrue(set.contains("Triggers"));
		assertEquals(10, set.size());
	}
}
