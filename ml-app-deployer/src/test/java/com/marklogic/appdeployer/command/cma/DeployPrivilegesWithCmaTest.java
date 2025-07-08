/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.cma;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.security.DeployPrivilegesCommand;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.PrivilegeManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeployPrivilegesWithCmaTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		initializeAppDeployer(new TestDeployPrivilegesCommand());

		PrivilegeManager mgr = new PrivilegeManager(manageClient);
		appConfig.getCmaConfig().setDeployPrivileges(true);

		try {
			deploySampleApp();
			assertTrue(mgr.exists("sample-app-execute-1"));
			assertTrue(mgr.exists("sample-app-execute-2"));

			deploySampleApp();
			assertTrue(mgr.exists("sample-app-execute-1"));
			assertTrue(mgr.exists("sample-app-execute-2"));
		} finally {
			initializeAppDeployer(new DeployPrivilegesCommand());

			undeploySampleApp();
			assertFalse(mgr.exists("sample-app-execute-1"));
			assertFalse(mgr.exists("sample-app-execute-2"));
		}
	}
}

class TestDeployPrivilegesCommand extends DeployPrivilegesCommand {
	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		// Returning null to force an error in case this is used at all
		return null;
	}
}
