package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.resource.security.RoleManager;
import org.junit.Test;

import java.io.File;

/**
 * Each of these tests verifies that the roles in the given config dir can be deployed successfully based on their
 * dependencies with one another.
 */
public class DeployRolesWithDependenciesTest extends AbstractAppDeployerTest {

	@Test
	public void roleWithPermissions() {
		appConfig.getConfigDir().setBaseDir(new File("src/test/resources/sample-app/roles-with-permissions"));

		initializeAppDeployer(new DeployRolesCommand());
		try {
			deploySampleApp();
		} finally {
			undeploySampleApp();
		}
	}

	/**
	 * This scenario has a mix of XML/JSON files (to verify that we parse each correctly) and a big jumble of roles.
	 */
	@Test
	public void testSorting() {
		appConfig.getConfigDir().setBaseDir(new File("src/test/resources/sample-app/roles-with-dependencies"));

		initializeAppDeployer(new DeployRolesCommand());
		try {
			deploySampleApp();
		} finally {
			undeploySampleApp();
		}
	}

	/**
	 * This scenario features role files that must be sorted multiple times in order for them to be in the correct
	 * order.
	 */
	@Test
	public void testEvenMoreRoles() {
		appConfig.getConfigDir().setBaseDir(new File("src/test/resources/sample-app/even-more-roles-with-dependencies"));

		initializeAppDeployer(new DeployRolesCommand());
		try {
			deploySampleApp();
		} finally {
			undeploySampleApp();
		}
	}

	/**
	 * This scenario tests that when two roles are next to each other with different dependencies - role1 and role2 -
	 * that we figure out that role1 has a dependency on a role closest to the end of the current list of files.
	 */
	@Test
	public void anotherSortingTest() {
		appConfig.getConfigDir().setBaseDir(new File("src/test/resources/sample-app/more-roles-with-dependencies"));

		initializeAppDeployer(new DeployRolesCommand());
		try {
			deploySampleApp();
		} finally {
			undeploySampleApp();
		}
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
