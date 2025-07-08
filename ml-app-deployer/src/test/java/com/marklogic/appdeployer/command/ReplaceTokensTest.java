/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.modules.LoadModulesCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.appdeployer.util.SimplePropertiesSource;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.modulesloader.impl.DefaultModulesLoader;
import com.marklogic.client.io.BytesHandle;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReplaceTokensTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		appConfig.setContentForestsPerHost(1);
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/token-test/ml-config")));
		appConfig.setModulePaths(Arrays.asList("src/test/resources/token-test/ml-modules"));

		Properties props = new Properties();
		props.setProperty("xdbcEnabled", "true");
		props.setProperty("sample-token", "replaced!");
		appConfig.populateCustomTokens(new SimplePropertiesSource(props));

		LoadModulesCommand loadModulesCommand = new LoadModulesCommand();
		loadModulesCommand.initializeDefaultModulesLoader(new CommandContext(appConfig, manageClient, adminManager));
		((DefaultModulesLoader) loadModulesCommand.getModulesLoader()).setModulesManager(null);

		initializeAppDeployer(new DeployRestApiServersCommand(true), loadModulesCommand);
		deploySampleApp();

		// We know xdbcEnabled was replaced, otherwise the deployment of the REST API server would have failed
		// Gotta verify the text in the module was replaced

		DatabaseClient modulesClient = appConfig.newAppServicesDatabaseClient(appConfig.getModulesDatabaseName());
		try {
			String moduleText = new String(modulesClient.newDocumentManager().read("/hello.xqy", new BytesHandle()).get());
			assertTrue(moduleText.contains("replaced!"), "Did not find replaced text in module: " + moduleText);
		} finally {
			modulesClient.release();
			undeploySampleApp();
		}
	}
}
