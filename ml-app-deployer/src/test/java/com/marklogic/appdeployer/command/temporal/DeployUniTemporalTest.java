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
package com.marklogic.appdeployer.command.temporal;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.mgmt.resource.temporal.TemporalAxesManager;
import com.marklogic.mgmt.resource.temporal.TemporalCollectionManager;
import com.marklogic.rest.util.ResourcesFragment;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeployUniTemporalTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/uni-temporal-config"));

		initializeAppDeployer(
			new DeployTemporalAxesCommand(), new DeployTemporalCollectionsCommand(), new DeployTemporalCollectionsLSQTCommand(),
			new DeployOtherDatabasesCommand(1));

		try {
			appDeployer.deploy(appConfig);

			verifyTemporalDocsExist(appConfig.getContentDatabaseName());
		} finally {
			undeploySampleApp();
		}
	}

	private void verifyTemporalDocsExist(String databaseName) {
		ResourcesFragment axes = new TemporalAxesManager(manageClient, databaseName).getAsXml();
		assertEquals(1, axes.getResourceCount());

		ResourcesFragment collections = new TemporalCollectionManager(manageClient, databaseName).getAsXml();
		assertEquals(1, collections.getResourceCount());
	}
}
