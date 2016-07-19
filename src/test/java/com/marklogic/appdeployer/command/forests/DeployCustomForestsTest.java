package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand;
import com.marklogic.mgmt.forests.ForestManager;
import org.junit.After;
import org.junit.Test;

import java.io.File;

/**
 * Verifies that directories under ./forests/ are processed correctly.
 */
public class DeployCustomForestsTest extends AbstractAppDeployerTest {

	@After
	public void tearDown() {
		//undeploySampleApp();
	}

	@Test
	public void test() {
		// To avoid hardcoding host names that might cause the test to fail, we use a custom token and assume that
		// the host of the Management API will work
		appConfig.getCustomTokens().put("%%CUSTOM_HOST%%", super.manageConfig.getHost());

		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/custom-forests")));

		initializeAppDeployer(new DeployContentDatabasesCommand(1), new DeployCustomForestsCommand());
		deploySampleApp();

		ForestManager mgr = new ForestManager(manageClient);
		assertTrue("One 'simple' forest should have been created by default", mgr.exists("sample-app-content-1"));
		assertTrue(mgr.exists("sample-app-content-custom-1"));
		assertTrue(mgr.exists("sample-app-content-custom-2"));
		assertTrue(mgr.exists("sample-app-content-custom-3"));
	}
}
