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
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ManagePartitionQueriesTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		appConfig.setAddHostNameTokens(true);
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/partition-queries")));

		initializeAppDeployer(new DeployOtherDatabasesCommand(1), new DeployPartitionsCommand(),
			new DeployPartitionQueriesCommand());

		deploySampleApp();

		try {
			List<String> forestNames = new DatabaseManager(manageClient).getForestNames("sample-app-content");
			assertEquals(3, forestNames.size());
			assertTrue(forestNames.contains("sample-app-content-1"));
			assertTrue(forestNames.contains("tier1-0001"));
			assertTrue(forestNames.contains("tier2-0001"));

			DatabaseClient client = appConfig.newAppServicesDatabaseClient("sample-app-content");
			GenericDocumentManager mgr = client.newDocumentManager();
			assertNotNull(mgr.readAs("http://marklogic.com/xdmp/partitions/1", String.class));
			assertNotNull(mgr.readAs("http://marklogic.com/xdmp/partitions/2", String.class));
		} finally {
			undeploySampleApp();
		}
	}
}
