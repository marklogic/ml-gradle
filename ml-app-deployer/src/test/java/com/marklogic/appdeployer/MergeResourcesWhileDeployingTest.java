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
package com.marklogic.appdeployer;

import com.marklogic.appdeployer.command.appservers.DeployOtherServersCommand;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.security.DeployPrivilegeRolesCommand;
import com.marklogic.appdeployer.command.security.DeployPrivilegesCommand;
import com.marklogic.appdeployer.command.security.DeployRolesCommand;
import com.marklogic.appdeployer.command.security.DeployUsersCommand;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.database.Database;
import com.marklogic.mgmt.api.group.Namespace;
import com.marklogic.mgmt.api.security.Privilege;
import com.marklogic.mgmt.api.security.Role;
import com.marklogic.mgmt.api.security.User;
import com.marklogic.mgmt.api.server.Server;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.mapper.ResourceMapper;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.resource.forests.ForestManager;
import com.marklogic.mgmt.resource.security.PrivilegeManager;
import com.marklogic.mgmt.resource.security.RoleManager;
import com.marklogic.mgmt.resource.security.UserManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MergeResourcesWhileDeployingTest extends AbstractAppDeployerTest {

	private ResourceMapper resourceMapper;

	@BeforeEach
	public void setup() {
		List<ConfigDir> list = new ArrayList<>();
		list.add(new ConfigDir(new File("src/test/resources/sample-app/multiple-config-paths/path1")));
		list.add(new ConfigDir(new File("src/test/resources/sample-app/multiple-config-paths/path2")));
		appConfig.setConfigDirs(list);

		resourceMapper = new DefaultResourceMapper(new API(manageClient));
	}

	@AfterEach
	public void teardown() {
		undeploySampleApp();
	}

	@Test
	public void users() {
		initializeAppDeployer(new DeployUsersCommand());
		deploySampleApp();

		UserManager mgr = new UserManager(manageClient);
		User user = resourceMapper.readResource(mgr.getAsJson("sample-app-jane"), User.class);
		assertEquals(2, user.getRole().size());
		assertEquals("rest-reader", user.getRole().get(0), "The role from the first path should be first");
		assertEquals("manage-user", user.getRole().get(1));
	}

	@Test
	public void roles() {
		initializeAppDeployer(new DeployRolesCommand());
		deploySampleApp();

		RoleManager mgr = new RoleManager(manageClient);
		Role role1 = resourceMapper.readResource(mgr.getAsJson("sample-app-role1"), Role.class);
		Role role2 = resourceMapper.readResource(mgr.getAsJson("sample-app-role2"), Role.class);

		assertEquals(2, role1.getRole().size());
		assertEquals("rest-reader", role1.getRole().get(0));
		assertEquals("manage-user", role1.getRole().get(1));

		assertEquals(2, role2.getRole().size());
		assertEquals("rest-writer", role2.getRole().get(0));
		assertEquals("sample-app-role1", role2.getRole().get(1));
	}

	@Test
	public void privileges() {
		initializeAppDeployer(new DeployPrivilegesCommand(), new DeployPrivilegeRolesCommand());
		deploySampleApp();

		PrivilegeManager mgr = new PrivilegeManager(manageClient);
		Privilege p = resourceMapper.readResource(mgr.getAsJson("sample-app-execute-1", "kind", "execute"), Privilege.class);
		assertEquals(2, p.getRole().size());
		assertEquals("rest-reader", p.getRole().get(0));
		assertEquals("manage-user", p.getRole().get(1));
	}

	@Test
	public void servers() {
		initializeAppDeployer(new DeployOtherServersCommand());
		deploySampleApp();

		ServerManager mgr = new ServerManager(manageClient);

		Server s = resourceMapper.readResource(mgr.getAsJson("sample-app-server"), Server.class);
		assertEquals("Documents", s.getModulesDatabase());
		assertEquals("Documents", s.getModulesDatabase());

		List<Namespace> namespaces = s.getNamespace();
		assertEquals(2, namespaces.size());
		assertEquals("test1", namespaces.get(0).getPrefix());
		assertEquals("test2", namespaces.get(1).getPrefix());
	}

	@Test
	public void databasesWithoutCma() {
		appConfig.getCmaConfig().setDeployDatabases(false);
		deployMultipleDatabasesNeededMergingAndVerify();
	}

	@Test
	public void databasesWithCma() {
		appConfig.getCmaConfig().setCombineRequests(true);
		appConfig.getCmaConfig().setDeployDatabases(true);
		deployMultipleDatabasesNeededMergingAndVerify();
	}

	/**
	 * Includes sub-databases so their creation can be verified when their parent database has files merged together.
	 */
	private void deployMultipleDatabasesNeededMergingAndVerify() {
		appConfig.setContentForestsPerHost(2);
		appConfig.setTestRestPort(appConfig.getRestPort() + 1);
		appConfig.getCmaConfig().setDeployForests(true);

		initializeAppDeployer(new DeployOtherDatabasesCommand());

		deploySampleApp();

		DatabaseManager mgr = new DatabaseManager(manageClient);

		Database db = resourceMapper.readResource(mgr.getAsJson("sample-app-one-database"), Database.class);
		assertEquals(2, db.getRangeElementIndex().size());
		assertEquals("id", db.getRangeElementIndex().get(0).getLocalname());
		assertEquals("otherId", db.getRangeElementIndex().get(1).getLocalname());
		assertTrue(db.getTripleIndex(), "The file in the second path should override single-value properties from the " +
			"file in the first path");
		assertEquals("sample-app-schema-database", db.getSchemaDatabase());
		assertEquals("sample-app-triggers-database", db.getTriggersDatabase());

		// Verify the main content database was created
		db = resourceMapper.readResource(mgr.getAsJson("sample-app-content"), Database.class);
		assertEquals(2, db.getRangeElementIndex().size());
		assertEquals("id", db.getRangeElementIndex().get(0).getLocalname());
		assertEquals("otherId", db.getRangeElementIndex().get(1).getLocalname());

		// Verify that the test content database was created
		db = resourceMapper.readResource(mgr.getAsJson("sample-app-test-content"), Database.class);
		assertEquals(2, db.getRangeElementIndex().size());
		assertEquals("id", db.getRangeElementIndex().get(0).getLocalname());
		assertEquals("otherId", db.getRangeElementIndex().get(1).getLocalname());

		assertTrue(mgr.exists("sample-app-schema-database"));
		assertTrue(mgr.exists("sample-app-triggers-database"));
		assertTrue(mgr.exists("sample-app-two-database"));

		assertTrue(mgr.exists("sample-app-one-database-subdb01"));
		assertTrue(mgr.exists("sample-app-one-database-subdb02"));

		ForestManager forestManager = new ForestManager(manageClient);
		assertTrue(forestManager.exists("sample-app-one-database-1"));
		assertTrue(forestManager.exists("sample-app-schema-database-1"));
		assertTrue(forestManager.exists("sample-app-triggers-database-1"));
		assertTrue(forestManager.exists("sample-app-two-database-1"));
		assertTrue(forestManager.exists("sample-app-one-database-subdb01-1"));
		assertTrue(forestManager.exists("sample-app-one-database-subdb02-1"));

		assertTrue(forestManager.exists("sample-app-content-1"));
		assertTrue(forestManager.exists("sample-app-content-2"));
		assertFalse(forestManager.exists("sample-app-content-3"));
		assertTrue(forestManager.exists("sample-app-test-content-1"));
		assertTrue(forestManager.exists("sample-app-test-content-2"));
		assertFalse(forestManager.exists("sample-app-test-content-3"));
	}
}
