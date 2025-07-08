/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.cma;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.appservers.DeployOtherServersCommand;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeployServersWithCmaTest extends AbstractAppDeployerTest {

	@AfterEach
	public void teardown() {
		initializeAppDeployer(new DeployOtherServersCommand());
		undeploySampleApp();
	}

	@Test
	public void withCma() {
		appConfig.getCmaConfig().setDeployServers(true);
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/other-servers")));

		ServerManager mgr = new ServerManager(manageClient);

		initializeAppDeployer(new TestDeployOtherServersCommand());

		deploySampleApp();
		assertTrue(mgr.exists("sample-app-odbc"));
		assertTrue(mgr.exists("sample-app-xdbc"));

		deploySampleApp();
		assertTrue(mgr.exists("sample-app-odbc"));
		assertTrue(mgr.exists("sample-app-xdbc"));
	}

}

class TestDeployOtherServersCommand extends DeployOtherServersCommand {

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		// This should force an error if CMA isn't used
		return null;
	}
}
