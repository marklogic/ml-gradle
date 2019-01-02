package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.UserManager;
import org.junit.Test;

public class DeployUsersWithCmaTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		UserManager userManager = new UserManager(manageClient);

		appConfig.setDeployUsersWithCma(true);

		initializeAppDeployer(new TestDeployUsersCommand());

		try {
			deploySampleApp();

			assertTrue(userManager.exists("sample-app-jane"));
			assertTrue(userManager.exists("sample-app-john"));
		} finally {
			initializeAppDeployer(new DeployUsersCommand());
			undeploySampleApp();

			assertFalse(userManager.exists("sample-app-jane"));
			assertFalse(userManager.exists("sample-app-john"));
		}
	}
}

class TestDeployUsersCommand extends DeployUsersCommand {

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		throw new RuntimeException("This shouldn't be called when CMA is used");
	}
}
