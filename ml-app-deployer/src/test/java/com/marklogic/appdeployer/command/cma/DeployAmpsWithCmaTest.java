/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.cma;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.security.DeployAmpsCommand;
import com.marklogic.mgmt.api.configuration.Configuration;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.AmpManager;
import org.junit.jupiter.api.Test;
import org.springframework.util.FileCopyUtils;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeployAmpsWithCmaTest extends AbstractAppDeployerTest {

	@Test
	public void test() throws Exception {
		initializeAppDeployer(new TestDeployAmpsCommand());

		ConfigDir dir = appConfig.getFirstConfigDir();
		File ampsDir = dir.getAmpsDir();
		final String amp1 = new String(FileCopyUtils.copyToByteArray(new File(ampsDir, "amp-1.json")));
		final String amp2 = new String(FileCopyUtils.copyToByteArray(new File(ampsDir, "amp-2.xml")));

		AmpManager mgr = new AmpManager(manageClient);

		appConfig.getCmaConfig().setDeployAmps(true);
		deploySampleApp();

		try {
			assertTrue(mgr.ampExists(amp1));
			assertTrue(mgr.ampExists(amp2));

			// On the second invocation, nothing should be sent.
			initializeAppDeployer(new SecondDeploymentDeployAmpsCommand());
			deploySampleApp();

			assertTrue(mgr.ampExists(amp1));
			assertTrue(mgr.ampExists(amp2));
		} finally {
			// Need to use the real command so that the undeploy can use the amps endpoint
			initializeAppDeployer(new DeployAmpsCommand());
			undeploySampleApp();

			assertFalse(mgr.ampExists(amp1));
			assertFalse(mgr.ampExists(amp2));
		}
	}
}

/**
 * This is used to ensure that CMA is used and not the amps endpoint.
 */
class TestDeployAmpsCommand extends DeployAmpsCommand {

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		throw new RuntimeException("This shouldn't be called when CMA is used");
	}

}

class SecondDeploymentDeployAmpsCommand extends TestDeployAmpsCommand {

	@Override
	protected void deployConfiguration(CommandContext context, Configuration config) {
		if (config.getAmps() != null && !config.getAmps().isEmpty()) {
			throw new RuntimeException("No amps should have been added during the second deployment as none of them changed");
		}
		super.deployConfiguration(context, config);
	}
}
