/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.resource.security.RoleManager;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DontUndeployCertainRolesTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		final String testRole = "ml-app-deployer-test-role";
		final String adminRole = "admin";

		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/users-to-not-undeploy"));
		initializeAppDeployer(new DeployRolesCommand());

		RoleManager mgr = new RoleManager(manageClient);
		assertFalse(mgr.exists(testRole));
		assertTrue(mgr.exists(adminRole));

		deploySampleApp();

		try {
			assertTrue(mgr.exists(testRole));
			assertTrue(mgr.exists(adminRole));
		} finally {
			undeploySampleApp();

			assertFalse(mgr.exists(testRole));
			assertTrue(mgr.exists(adminRole), "The 'admin' role should not have been deleted since it's in the list of " +
				"roles to not undeploy");
		}
	}

	@Test
	public void verifySetOfDefaultRoles() {
		Set<String> roles = new DeployRolesCommand().getDefaultRolesToNotUndeploy();
		assertEquals(3, roles.size(), "The main role we don't want to delete is admin, but manage-admin and " +
			"security are included as well just to be safe, as those two roles together can allow for any other " +
			"role to be recreated");
		assertTrue(roles.contains("admin"));
		assertTrue(roles.contains("manage-admin"));
		assertTrue(roles.contains("security"));
	}

}
