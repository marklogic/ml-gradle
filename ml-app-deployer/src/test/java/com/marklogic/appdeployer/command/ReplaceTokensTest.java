/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
