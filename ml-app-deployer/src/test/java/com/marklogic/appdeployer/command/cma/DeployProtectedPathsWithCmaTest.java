/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.cma;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.security.DeployProtectedPathsCommand;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.ProtectedPathManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeployProtectedPathsWithCmaTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		initializeAppDeployer(new TestDeployProtectedPathsCommand());

		ProtectedPathManager mgr = new ProtectedPathManager(manageClient);
		appConfig.getCmaConfig().setDeployProtectedPaths(true);

		try {
			deploySampleApp();
			assertTrue(mgr.exists("/test:element"));

			deploySampleApp();
			assertTrue(mgr.exists("/test:element"));
		} finally {
			initializeAppDeployer(new DeployProtectedPathsCommand());

			undeploySampleApp();
			assertFalse(mgr.exists("/test:element"));
		}
	}
}

class TestDeployProtectedPathsCommand extends DeployProtectedPathsCommand {
	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		// Returning null to force an error in case this is used at all
		return null;
	}
}
