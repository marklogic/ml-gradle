package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.resource.security.RoleManager;
import org.junit.Test;

import java.io.File;

public class DeployRoleThatRefersToItselfTest extends AbstractAppDeployerTest {

	/**
	 * Tests both an XML file and a JSON file so we can verify that each type can be deserialized correctly.
	 */
	@Test
	public void test() {
		appConfig.getConfigDir().setBaseDir(new File("src/test/resources/sample-app/role-refers-to-itself"));
		initializeAppDeployer(new DeployRolesCommand());

		try {
			appDeployer.deploy(appConfig);

			RoleManager mgr = new RoleManager(manageClient);
			assertTrue(mgr.exists("sample-app-json-role"));
			assertTrue(mgr.exists("sample-app-xml-role"));

		} finally {
			undeploySampleApp();
		}
	}

}
