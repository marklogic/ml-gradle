package com.marklogic.appdeployer;

import com.marklogic.appdeployer.command.appservers.DeployOtherServersCommand;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MergeResourcesWhileDeployingTest extends AbstractAppDeployerTest {

	private ResourceMapper resourceMapper;

	@Before
	public void setup() {
		List<ConfigDir> list = new ArrayList<>();
		list.add(new ConfigDir(new File("src/test/resources/sample-app/multiple-config-paths/path1")));
		list.add(new ConfigDir(new File("src/test/resources/sample-app/multiple-config-paths/path2")));
		appConfig.setConfigDirs(list);

		resourceMapper = new DefaultResourceMapper(new API(manageClient));
	}

	@After
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
		assertEquals("The role from the first path should be first", "rest-reader", user.getRole().get(0));
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
		initializeAppDeployer(new DeployPrivilegesCommand());
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
		appConfig.setDeployDatabasesWithCma(false);
		deployMultipleDatabasesNeededMergingAndVerify();
	}

	@Test
	public void databasesWithCma() {
		appConfig.setDeployDatabasesWithCma(true);
		deployMultipleDatabasesNeededMergingAndVerify();
	}

	/**
	 * Includes sub-databases so their creation can be verified when their parent database has files merged together.
	 */
	private void deployMultipleDatabasesNeededMergingAndVerify() {
		appConfig.setContentForestsPerHost(2);
		appConfig.setTestRestPort(appConfig.getRestPort() + 1);
		appConfig.setDeployForestsWithCma(true);

		initializeAppDeployer(new DeployOtherDatabasesCommand());
		deploySampleApp();

		DatabaseManager mgr = new DatabaseManager(manageClient);

		Database db = resourceMapper.readResource(mgr.getAsJson("sample-app-one-database"), Database.class);
		assertEquals(2, db.getRangeElementIndex().size());
		assertEquals("id", db.getRangeElementIndex().get(0).getLocalname());
		assertEquals("otherId", db.getRangeElementIndex().get(1).getLocalname());
		assertTrue("The file in the second path should override single-value properties from the " +
			"file in the first path", db.getTripleIndex());
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
