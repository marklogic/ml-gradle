package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.ResourceFileManagerImpl;
import com.marklogic.appdeployer.command.ResourceFilenameFilter;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.security.RoleManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class IncrementallyDeployRolesTest extends AbstractAppDeployerTest {

	private final static String ROLE_INSTALL = "sampleapp-install";
	private final static String ROLE_MODULES = "sampleapp-modules";

	private RoleManager roleManager;
	private DeployRolesCommand deployRolesCommand;
	private ManageClient originalManageClient;

	@Before
	public void setup() {
		appConfig.setIncrementalDeploy(true);
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/roles-with-permissions"));

		roleManager = new RoleManager(manageClient);
		assertRolesDontExist();

		originalManageClient = this.manageClient;
		deployRolesCommand = new DeployRolesCommand();
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
		initializeAppDeployer(deployRolesCommand);
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
		initializeAppDeployer(deployRolesCommand);
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

	/**
	 * The properties file may exist from a previous run of the test, so this is used to delete it.
	 */
	private void deleteResourceTimestampsFile() {
		ResourceFilenameFilter filter = (ResourceFilenameFilter) deployRolesCommand.getResourceFilenameFilter();
		ResourceFileManagerImpl manager = (ResourceFileManagerImpl) filter.getResourceFileManager();
		manager.deletePropertiesFile();
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
