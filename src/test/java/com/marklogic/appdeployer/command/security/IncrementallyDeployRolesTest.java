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

import com.marklogic.appdeployer.command.AbstractIncrementalDeployTest;
import com.marklogic.mgmt.resource.security.RoleManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class IncrementallyDeployRolesTest extends AbstractIncrementalDeployTest {

	private final static String ROLE_INSTALL = "sampleapp-install";
	private final static String ROLE_MODULES = "sampleapp-modules";

	private RoleManager roleManager;

	@BeforeEach
	public void setup() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/roles-with-permissions"));
		roleManager = new RoleManager(manageClient);
		assertRolesDontExist();

		// Have to turn resource merging off for this to work
		appConfig.setMergeResources(false);
	}

	@AfterEach
	public void teardown() {
		this.manageClient = originalManageClient;
		initializeAppDeployer(new DeployRolesCommand());
		undeploySampleApp();
		assertRolesDontExist();
	}

	@Test
	public void filesShouldNotBeDeployedDuringSecondDeployment() {
		initializeAppDeployer(new DeployRolesCommand());
		deleteResourceTimestampsFile();
		deploySampleApp();
		assertRolesExist();

		// Ensure that no calls can be made to the Manage API. The deployment should succeed because neither of
		// the role files have been modified.
		this.manageClient = null;
		initializeAppDeployer(new DeployRolesCommand());
		deploySampleApp();
		assertRolesExist();
	}

	@Test
	public void filesShouldBeDeployedDuringSecondDeployment() {
		initializeAppDeployer(new DeployRolesCommand());
		deleteResourceTimestampsFile();
		deploySampleApp();
		assertRolesExist();

		this.manageClient = null;
		deleteResourceTimestampsFile();
		initializeAppDeployer(new DeployRolesCommand());
		try {
			deploySampleApp();
			fail("The deployment should have failed because the manageClient is null and the resource timestamps file " +
				"was deleted, causing the command to try to deploy both roles");
		} catch (NullPointerException ex) {
			logger.info("Caught expected NullPointerException, as manageClient was set to be null: " + ex.getMessage());
			assertRolesExist();
		}
	}

	private void assertRolesExist() {
		assertTrue(roleManager.exists(ROLE_INSTALL));
		assertTrue(roleManager.exists(ROLE_MODULES));
	}

	private void assertRolesDontExist() {
		assertFalse(roleManager.exists(ROLE_INSTALL));
		assertFalse(roleManager.exists(ROLE_MODULES));
	}
}
