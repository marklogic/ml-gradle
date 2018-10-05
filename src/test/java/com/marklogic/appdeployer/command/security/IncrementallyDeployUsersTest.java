package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.security.UserManager;
import org.junit.After;
import org.junit.Test;

public class IncrementallyDeployUsersTest extends AbstractAppDeployerTest {

	private ManageClient originalManageClient;

	@After
	public void teardown() {
		this.manageClient = originalManageClient;
		undeploySampleApp();
		assertUsersDontExist(new UserManager(manageClient));
	}

	@Test
	public void test() {
		this.originalManageClient = this.manageClient;
		UserManager userManager = new UserManager(manageClient);
		assertUsersDontExist(userManager);

		initializeAppDeployer(new DeployUsersCommand());
		deploySampleApp();
		assertUsersExist(userManager);

		// Ensure that no calls can be made to the Manage API. The deployment should succeed because neither of
		// the role files have been modified.
		this.manageClient = null;
		deploySampleApp();
		assertUsersExist(userManager);
	}

	private void assertUsersExist(UserManager userManager) {
		assertTrue(userManager.exists("sample-app-jane"));
		assertTrue(userManager.exists("sample-app-john"));
	}

	private void assertUsersDontExist(UserManager userManager) {
		assertFalse(userManager.exists("sample-app-jane"));
		assertFalse(userManager.exists("sample-app-john"));
	}
}
