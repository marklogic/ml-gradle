package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.mgmt.security.RoleManager;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class DeployRolesWithDependenciesTest extends AbstractAppDeployerTest {

	/**
	 * This scenario has a mix of XML/JSON files (to verify that we parse each correctly) and a big jumble of roles.
	 */
	@Test
	public void testSorting() {
		appConfig.getConfigDir().setBaseDir(new File("src/test/resources/sample-app/roles-with-dependencies"));

		DeployRolesCommand command = new DeployRolesCommand();
		File[] files = command.listFilesInDirectory(appConfig.getConfigDir().getRolesDir(),
			new CommandContext(appConfig, manageClient, null));

		assertEquals("role3.json", files[0].getName());
		assertEquals("role2.xml", files[1].getName());
		assertEquals("role1.json", files[2].getName());
		assertEquals("role4.xml", files[3].getName());
		assertEquals("role5.json", files[4].getName());
		assertEquals("role0.json", files[5].getName());
	}

	/**
	 * This scenario tests that when two roles are next to each other with different dependencies - role1 and role2 -
	 * that we figure out that role1 has a dependency on a role closest to the end of the current list of files.
	 */
	@Test
	public void anotherSortingTest() {
		appConfig.getConfigDir().setBaseDir(new File("src/test/resources/sample-app/more-roles-with-dependencies"));

		DeployRolesCommand command = new DeployRolesCommand();
		File[] files = command.listFilesInDirectory(appConfig.getConfigDir().getRolesDir(),
			new CommandContext(appConfig, manageClient, null));

		assertEquals("role0.json", files[0].getName());
		assertEquals("role2.json", files[1].getName());
		assertEquals("role3.json", files[2].getName());
		assertEquals("role1.json", files[3].getName());
	}

	@Test
	public void test() {
		appConfig.getConfigDir().setBaseDir(new File("src/test/resources/sample-app/roles-with-dependencies"));
		initializeAppDeployer(new DeployRolesCommand());

		try {
			appDeployer.deploy(appConfig);

			RoleManager mgr = new RoleManager(manageClient);
			for (int i = 0; i <= 5; i++) {
				assertTrue(mgr.exists("sample-app-role" + i));
			}
		} finally {
			undeploySampleApp();
		}
	}
}
