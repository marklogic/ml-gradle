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
package com.marklogic.appdeployer.command.rebalancer;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.CommandMapBuilder;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.resource.rebalancer.PartitionManager;
import com.marklogic.mgmt.resource.rebalancer.PartitionProperties;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ManagePartitionsTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		List<Command> commands = new CommandMapBuilder().buildCommandMap().get("mlRebalancerCommands");

		assertEquals(2, commands.size());
		assertTrue(commands.get(0) instanceof DeployPartitionsCommand);
		assertTrue(commands.get(1) instanceof DeployPartitionQueriesCommand);

		appConfig.setAddHostNameTokens(true);
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/partitions")));

		initializeAppDeployer(new DeployOtherDatabasesCommand(1), new DeployPartitionsCommand());
		deploySampleApp();

		try {
			List<String> forestNames = new DatabaseManager(manageClient).getForestNames("sample-app-content");
			assertEquals(3, forestNames.size());
			assertTrue(forestNames.contains("sample-app-content-1"));
			assertTrue(forestNames.contains("myDate-2011-0001"));
			assertTrue(forestNames.contains("myDate-2012-0001"));

			verifyPartitionCanBeTakenOffline();
		} finally {
			undeploySampleApp();
		}
	}

	private void verifyPartitionCanBeTakenOffline() {
		final String partitionName = "myDate-2011";

		PartitionManager mgr = new PartitionManager(manageClient, "sample-app-content");
		PartitionProperties props = mgr.getPartitionProperties(partitionName);
		assertTrue(props.isOnline());
		assertEquals("all", props.getUpdatesAllowed());

		mgr.takePartitionOffline(partitionName);
		props = mgr.getPartitionProperties(partitionName);
		assertTrue(props.isOffline());

		mgr.takePartitionOnline(partitionName);
		props = mgr.getPartitionProperties(partitionName);
		assertTrue(props.isOnline());
	}
}
