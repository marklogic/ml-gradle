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
package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.triggers.DeployTriggersCommand;
import com.marklogic.mgmt.resource.triggers.TriggerManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeployTriggersToMultipleDatabasesTest extends AbstractAppDeployerTest {

	@AfterEach
	public void teardown() {
		undeploySampleApp();
	}

	@Test
	public void test() {
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/multiple-triggers-databases")));

		initializeAppDeployer(new DeployOtherDatabasesCommand(1), new DeployTriggersCommand());

		deploySampleApp();

		TriggerManager triggerManager = new TriggerManager(manageClient, appConfig.getTriggersDatabaseName());
		assertTrue(triggerManager.exists("my-trigger"));

		triggerManager = new TriggerManager(manageClient, "other-" + appConfig.getTriggersDatabaseName());
		assertTrue(triggerManager.exists("other-trigger"));

		triggerManager = new TriggerManager(manageClient, "third-" + appConfig.getTriggersDatabaseName());
		assertTrue(triggerManager.exists("third-trigger"));
	}
}
