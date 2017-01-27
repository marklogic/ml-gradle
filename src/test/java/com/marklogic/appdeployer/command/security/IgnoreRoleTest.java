package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.security.RoleManager;
import org.junit.Test;

public class IgnoreRoleTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		appConfig.setResourceFilenamesToIgnore("sample-app-role2.xml");
		initializeAppDeployer(new DeployRolesCommand());
		appDeployer.deploy(appConfig);

		try {
			RoleManager mgr = new RoleManager(manageClient);
			assertTrue(mgr.exists("sample-app-role1"));
			assertFalse("Role should not have been created because its resource file was ignored", mgr.exists("sample-app-role2"));
		} finally {
			undeploySampleApp();
		}
	}
}
