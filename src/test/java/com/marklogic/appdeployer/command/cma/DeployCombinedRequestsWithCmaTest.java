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
package com.marklogic.appdeployer.command.cma;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.appservers.DeployOtherServersCommand;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.security.*;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.security.Amp;
import com.marklogic.mgmt.api.security.Role;
import com.marklogic.mgmt.api.security.User;
import com.marklogic.mgmt.api.security.queryroleset.QueryRoleset;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.resource.forests.ForestManager;
import com.marklogic.mgmt.resource.security.PrivilegeManager;
import com.marklogic.mgmt.resource.security.ProtectedPathManager;
import com.marklogic.mgmt.resource.security.RoleManager;
import com.marklogic.mgmt.resource.security.UserManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DeployCombinedRequestsWithCmaTest extends AbstractAppDeployerTest {

	@AfterEach
	public void teardown() {
		undeploySampleApp();
	}

	@Test
	public void test() {
		List<ConfigDir> list = new ArrayList<>();
		list.add(new ConfigDir(new File("src/test/resources/cma-project")));
		appConfig.setConfigDirs(list);
		appConfig.getCmaConfig().enableAll();

		initializeAppDeployer(
			new DeployPrivilegesCommand(), new DeployRolesCommand(), new DeployUsersCommand(), new DeployAmpsCommand(),
			new DeployProtectedPathsCommand(), new DeployQueryRolesetsCommand(),
			new DeployOtherDatabasesCommand(2), new DeployOtherServersCommand());

		long start = System.currentTimeMillis();
		deploySampleApp();
		logger.info("TIME: " + (System.currentTimeMillis() - start));
		verifyResources();

		deploySampleApp();
		logger.info("TIME: " + (System.currentTimeMillis() - start));
		verifyResources();
	}

	private void verifyResources() {
		API api = new API(manageClient);

		PrivilegeManager privilegeManager = new PrivilegeManager(manageClient);
		assertTrue(privilegeManager.exists("sample-app-execute-1"));
		assertTrue(privilegeManager.exists("sample-app-execute-2"));

		RoleManager roleManager = new RoleManager(manageClient);
		assertTrue(roleManager.exists("sample-app-role1"));
		assertTrue(roleManager.exists("sample-app-role2"));

		Role role2 = api.role("sample-app-role2");
		assertNotNull(role2.getRole(), "sample-app-role2 should have a dependent role");
		assertEquals("sample-app-role1", role2.getRole().get(0));

		Role role3 = api.role("sample-app-role3");
		assertNotNull(role3.getPermission());
		assertEquals("sample-app-role3", role3.getPermission().get(0).getRoleName());

		UserManager userManager = new UserManager(manageClient);
		assertTrue(userManager.exists("sample-app-jane"));
		assertTrue(userManager.exists("sample-app-john"));
		User jane = api.user("sample-app-jane");
		assertEquals("manage-user", jane.getRole().get(0));
		assertEquals("sample-app-role2", jane.getRole().get(1));

		ProtectedPathManager pathManager = new ProtectedPathManager(manageClient);
		assertTrue(pathManager.exists("/test:element"));

		QueryRoleset queryRoleset = api.queryRoleset("view-admin", "flexrep-user");
		assertTrue(queryRoleset.exists());

		DatabaseManager dbManager = new DatabaseManager(manageClient);
		assertTrue(dbManager.exists("sample-app-content"));
		assertTrue(dbManager.exists("sample-app-modules"));
		assertTrue(dbManager.exists("sample-app-schemas"));
		assertTrue(dbManager.exists("sample-app-triggers"));

		ForestManager forestManager = new ForestManager(manageClient);
		assertTrue(forestManager.exists("sample-app-content-1"));
		assertTrue(forestManager.exists("sample-app-content-2"));
		assertFalse(forestManager.exists("sample-app-content-3"));
		assertTrue(forestManager.exists("sample-app-modules-1"));
		assertTrue(forestManager.exists("sample-app-modules-2"));
		assertFalse(forestManager.exists("sample-app-modules-3"));
		assertTrue(forestManager.exists("sample-app-schemas-1"));
		assertTrue(forestManager.exists("sample-app-schemas-2"));
		assertFalse(forestManager.exists("sample-app-schemas-3"));
		assertTrue(forestManager.exists("sample-app-triggers-1"));
		assertTrue(forestManager.exists("sample-app-triggers-2"));
		assertFalse(forestManager.exists("sample-app-triggers-3"));

		Amp amp1 = api.amp("function1", "org:example", "/module/path/name1", appConfig.getModulesDatabaseName());
		assertEquals("rest-reader", amp1.getRole().get(0));
		Amp amp2 = api.amp("function2", "org:example", "/module/path/name2", appConfig.getModulesDatabaseName());
		assertEquals("rest-writer", amp2.getRole().get(0));

		ServerManager serverManager = new ServerManager(manageClient);
		assertTrue(serverManager.exists("sample-app-server1"));
		assertTrue(serverManager.exists("sample-app-server2"));
		assertTrue(serverManager.exists("sample-app-server3"));
	}
}
