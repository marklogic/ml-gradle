package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.mgmt.resource.forests.ForestManager;
import org.junit.After;
import org.junit.Test;

import java.io.File;

/**
 * Verifies that directories under ./forests/ are processed correctly.
 */
public class DeployCustomForestsTest extends AbstractAppDeployerTest {

	@After
	public void tearDown() {
		undeploySampleApp();
	}

	@Test
	public void test() {
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/custom-forests")));

		initializeAppDeployer(new DeployOtherDatabasesCommand(1), new DeployCustomForestsCommand());
		deploySampleApp();

		ForestManager mgr = new ForestManager(manageClient);
		assertFalse("A default forest should not have been created since custom forests exist", mgr.exists("sample-app-content-1"));
		assertTrue(mgr.exists("sample-app-content-custom-1"));
		assertTrue(mgr.exists("sample-app-content-custom-2"));
		assertTrue(mgr.exists("sample-app-content-custom-3"));
	}

	@Test
	public void deployWithCma() {
		appConfig.getCmaConfig().enableAll();
		test();
	}
}
