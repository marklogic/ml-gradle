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
import com.marklogic.appdeployer.command.groups.DeployGroupsCommand;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateRestApiInOtherGroupTest extends AbstractAppDeployerTest {

	@AfterEach
	public void tearDown() {
		undeploySampleApp();
	}

	@Test
	public void test() {
		final String groupName = "ml-app-deployer-other-group";
		final String serverName = appConfig.getRestServerName();

		appConfig.setGroupName(groupName);
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/rest-api-other-group"));
		initializeAppDeployer(new DeployGroupsCommand(), new DeployRestApiServersCommand());

		deploySampleApp();
		assertTrue(new ServerManager(manageClient, groupName).exists(serverName));
		assertFalse(new ServerManager(manageClient).exists(serverName));

		deploySampleApp();
		assertTrue(new ServerManager(manageClient, groupName).exists(serverName),
			"The deployment should have succeeded because RestApiManager now checks to see if the REST API server " +
				"exists in the group defined by appConfig.getGroupName as opposed to just the Default group");
		assertFalse(new ServerManager(manageClient).exists(serverName));
	}
}
