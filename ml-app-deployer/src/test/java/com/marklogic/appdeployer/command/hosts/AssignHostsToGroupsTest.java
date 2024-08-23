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
package com.marklogic.appdeployer.command.hosts;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.groups.DeployGroupsCommand;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.server.Server;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import com.marklogic.mgmt.resource.hosts.HostManager;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssignHostsToGroupsTest extends AbstractAppDeployerTest {

	@Test
	public void assignSingleHostToExistingGroup() {
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/other-group")));

		HostManager hostManager = new HostManager(manageClient);

		final String otherGroup = "sample-app-other-group";
		final String hostname = hostManager.getHostNames().get(0);

		Map<String, String> hostGroups = new HashMap<>();
		hostGroups.put(hostname, otherGroup);
		appConfig.setHostGroups(hostGroups);

		initializeAppDeployer(new DeployGroupsCommand(), new AssignHostsToGroupsCommand());

		try {
			deploySampleApp();

			assertEquals(otherGroup, hostManager.getAssignedGroupName(hostname));

			ServerManager serverManager = new ServerManager(manageClient, otherGroup);
			assertTrue(serverManager.exists("Admin"));
			assertTrue(serverManager.exists("Manage"));
			assertTrue(serverManager.exists("App-Services"));
			assertTrue(serverManager.exists("HealthCheck"));

			Server adminServer = new API(manageClient).server("Admin", otherGroup);
			assertEquals(otherGroup, adminServer.getGroupName());
			assertEquals("rewriter.xqy", adminServer.getUrlRewriter(),
				"Verifying that the rewriter was set correctly for the new Admin server");
		} finally {
			undeploySampleApp();

			assertEquals("Default", hostManager.getAssignedGroupName(hostname),
				"The host's group should have been set back to Default");
		}
	}
}
