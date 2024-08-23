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
package com.marklogic.appdeployer.command.restapis;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.mgmt.resource.restapis.RestApiManager;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class DontCreateRestApiTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/db-only-config"));
		appConfig.setNoRestServer(true);

		initializeAppDeployer(new DeployRestApiServersCommand(), new DeployOtherDatabasesCommand(1));

		try {
			appDeployer.deploy(appConfig);
			RestApiManager mgr = new RestApiManager(manageClient);
			assertFalse(mgr.restApiServerExists(appConfig.getRestServerName()), "A REST API server should not have been created");
		} finally {
			undeploySampleApp();
		}
	}
}
