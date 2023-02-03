/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.resource.security.RoleManager;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Each of these tests verifies that the roles in the given config dir can be deployed successfully based on their
 * dependencies with one another.
 */
public class DeployRolesWithDependenciesTest extends AbstractAppDeployerTest {

	@Test
	public void roleWithPermissions() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/roles-with-permissions"));

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
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/roles-with-dependencies"));

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
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/even-more-roles-with-dependencies"));

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
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/more-roles-with-dependencies"));

		initializeAppDeployer(new DeployRolesCommand());
		try {
			deploySampleApp();
		} finally {
			undeploySampleApp();
		}
	}

	@Test
	void circularDependencies() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/roles-with-circular-dependencies"));
		initializeAppDeployer(new DeployRolesCommand());
		try {
			deploySampleApp();
			fail("Expected an error due to a circular dependency between the two roles");
		} catch (IllegalArgumentException ex) {
			assertEquals("Unable to deploy roles due to circular dependencies between two or more roles; " +
					"please remove these circular dependencies in order to deploy your roles",
				ex.getMessage());
		} finally {
			undeploySampleApp();
		}
	}

	@Test
	public void test() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/roles-with-dependencies"));
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
