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

import com.marklogic.appdeployer.CmaConfig;
import com.marklogic.appdeployer.command.AbstractIncrementalDeployTest;
import com.marklogic.appdeployer.command.ResourceFileManagerImpl;
import com.marklogic.mgmt.resource.security.RoleManager;
import com.marklogic.mgmt.resource.security.UserManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class IncrementallyDeployUsersTest extends AbstractIncrementalDeployTest {

	private UserManager userManager;
	private RoleManager roleManager;

	@BeforeEach
	public void setup() {
		userManager = new UserManager(manageClient);
		roleManager = new RoleManager(manageClient);

		// Have to turn resource merging off for this to work
		appConfig.setMergeResources(false);

		// And turn off CMA
		appConfig.setCmaConfig(new CmaConfig());
	}

	@AfterEach
	public void teardown() {
		this.manageClient = originalManageClient;
		initializeAppDeployer(new DeployUsersCommand(), new DeployRolesCommand());
		undeploySampleApp();
		assertUsersDontExist();
		assertRolesDontExist();
	}

	@Test
	public void test() {
		this.originalManageClient = this.manageClient;
		assertUsersDontExist();

		initializeAppDeployer(new DeployUsersCommand());
		deploySampleApp();
		assertUsersExist();

		// Ensure that no calls can be made to the Manage API. The deployment should succeed because neither of
		// the role files have been modified.
		this.manageClient = null;
		initializeAppDeployer(new DeployUsersCommand());
		deploySampleApp();
		assertUsersExist();
	}

	@Test
	public void usersAndRoles() throws IOException {
		this.originalManageClient = this.manageClient;

		assertUsersDontExist();
		assertRolesDontExist();

		initializeAppDeployer(new DeployUsersCommand(), new DeployRolesCommand());
		deploySampleApp();
		assertUsersExist();
		assertRolesExist();

		this.manageClient = null;
		initializeAppDeployer(new DeployUsersCommand(), new DeployRolesCommand());
		deploySampleApp();
		assertUsersExist();
		assertRolesExist();

		Properties props = new Properties();
		FileReader reader = new FileReader(ResourceFileManagerImpl.DEFAULT_FILE_PATH);
		props.load(reader);
		reader.close();
		assertEquals(4, props.size(), "There should be 2 entries for users and 2 entries for roles");
	}

	private void assertUsersExist() {
		assertTrue(userManager.exists("sample-app-jane"));
		assertTrue(userManager.exists("sample-app-john"));
	}

	private void assertUsersDontExist() {
		assertFalse(userManager.exists("sample-app-jane"));
		assertFalse(userManager.exists("sample-app-john"));
	}

	private void assertRolesExist() {
		assertTrue(roleManager.exists("sample-app-role1"));
		assertTrue(roleManager.exists("sample-app-role2"));
	}

	private void assertRolesDontExist() {
		assertFalse(roleManager.exists("sample-app-role1"));
		assertFalse(roleManager.exists("sample-app-role2"));
	}
}
