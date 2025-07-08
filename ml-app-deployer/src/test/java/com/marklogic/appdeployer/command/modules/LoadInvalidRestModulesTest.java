/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.modules;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.client.ext.modulesloader.impl.DefaultModulesLoader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class LoadInvalidRestModulesTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/db-only-config"));

		appConfig.setModulePaths(Arrays.asList("src/test/resources/sample-app/bad-modules"));

		LoadModulesCommand loadModulesCommand = new LoadModulesCommand();
		loadModulesCommand.initializeDefaultModulesLoader(new CommandContext(appConfig, manageClient, null));
		DefaultModulesLoader modulesLoader = (DefaultModulesLoader) loadModulesCommand.getModulesLoader();
		modulesLoader.setModulesManager(null);

		initializeAppDeployer(new DeployRestApiServersCommand(), new DeployOtherDatabasesCommand(1), loadModulesCommand);

		try {
			deploySampleApp();
			fail("The invalid search options file should have caused the LoadModulesCommand to fail");
		} catch (RuntimeException re) {
			assertTrue(re.getMessage().contains("RESTAPI-INVALIDCONTENT"), "Unexpected message: " + re.getMessage());
		} finally {
			undeploySampleApp();
		}
	}
}
