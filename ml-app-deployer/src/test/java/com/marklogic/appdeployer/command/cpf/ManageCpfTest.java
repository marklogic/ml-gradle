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
package com.marklogic.appdeployer.command.cpf;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.mgmt.resource.cpf.CpfConfigManager;
import com.marklogic.mgmt.resource.cpf.DomainManager;
import com.marklogic.mgmt.resource.cpf.PipelineManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ManageCpfTest extends AbstractAppDeployerTest {

	@AfterEach
	public void teardown() {
		undeploySampleApp();
	}

	@Test
	public void test() {
		initializeAppDeployer(new DeployRestApiServersCommand(),
			new DeployOtherDatabasesCommand(1), new DeployDomainsCommand(),
			new DeployCpfConfigsCommand(), new DeployPipelinesCommand());

		appDeployer.deploy(appConfig);

		String dbName = appConfig.getTriggersDatabaseName();
		assertEquals(2, new DomainManager(manageClient, dbName).getAsXml().getResourceCount());
		assertEquals(1, new CpfConfigManager(manageClient, dbName).getAsXml().getResourceCount());
		assertEquals(1, new PipelineManager(manageClient, dbName).getAsXml().getResourceCount());
	}

}
