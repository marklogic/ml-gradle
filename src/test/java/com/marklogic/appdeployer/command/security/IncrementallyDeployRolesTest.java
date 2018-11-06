package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractIncrementalDeployTest;
import com.marklogic.mgmt.resource.security.RoleManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class IncrementallyDeployRolesTest extends AbstractIncrementalDeployTest {

	private final static String ROLE_INSTALL = "sampleapp-install";
	private final static String ROLE_MODULES = "sampleapp-modules";

	private RoleManager roleManager;

	@Before
	public void setup() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/roles-with-permissions"));
		roleManager = new RoleManager(manageClient);
		assertRolesDontExist();
	}

	@After
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
