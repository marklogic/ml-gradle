package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.resource.security.UserManager;
import org.junit.Test;

public class DeployUsersViaCmaTest extends AbstractAppDeployerTest {

	/**
	 * Can't really verify that CMA was used other than looking at the logging. Can still verify that the users were
	 * created.
	 */
	@Test
	public void test() {
		UserManager userManager = new UserManager(manageClient);

		appConfig.setOptimizeWithCma(true);

		initializeAppDeployer(new DeployUsersCommand());

		try {
			deploySampleApp();

			assertTrue(userManager.exists("sample-app-jane"));
			assertTrue(userManager.exists("sample-app-john"));
		} finally {
			undeploySampleApp();

			assertFalse(userManager.exists("sample-app-jane"));
			assertFalse(userManager.exists("sample-app-john"));
		}
	}
}
