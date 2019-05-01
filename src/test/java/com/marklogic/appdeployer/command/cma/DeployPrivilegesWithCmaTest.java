package com.marklogic.appdeployer.command.cma;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.security.DeployPrivilegesCommand;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.PrivilegeManager;
import org.junit.Test;

public class DeployPrivilegesWithCmaTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		initializeAppDeployer(new TestDeployPrivilegesCommand());

		PrivilegeManager mgr = new PrivilegeManager(manageClient);
		appConfig.getCmaConfig().setDeployPrivileges(true);

		try {
			deploySampleApp();
			assertTrue(mgr.exists("sample-app-execute-1"));
			assertTrue(mgr.exists("sample-app-execute-1"));

			deploySampleApp();
			assertTrue(mgr.exists("sample-app-execute-1"));
			assertTrue(mgr.exists("sample-app-execute-1"));
		} finally {
			initializeAppDeployer(new DeployPrivilegesCommand());

			undeploySampleApp();
			assertFalse(mgr.exists("sample-app-execute-1"));
			assertFalse(mgr.exists("sample-app-execute-1"));
		}
	}
}

class TestDeployPrivilegesCommand extends DeployPrivilegesCommand {
	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		// Returning null to force an error in case this is used at all
		return null;
	}
}
