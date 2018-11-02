package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import org.junit.Test;

import java.io.File;

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
}
