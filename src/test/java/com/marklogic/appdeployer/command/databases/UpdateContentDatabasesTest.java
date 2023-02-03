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
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.rest.util.Fragment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UpdateContentDatabasesTest extends AbstractAppDeployerTest {

	private DatabaseManager dbMgr;
	private String idRangeIndexPath = "//m:range-element-index[m:scalar-type = 'string' and m:namespace-uri = 'urn:sampleapp' and m:localname='id' and m:collation='http://marklogic.com/collation/']";

	@BeforeEach
	public void setup() {
		dbMgr = new DatabaseManager(manageClient);
	}

	@AfterEach
	public void teardown() {
		undeploySampleApp();
	}

	@Test
	public void updateDatabase() {
		// We want both a main and a test app server in this test
		appConfig.setTestRestPort(SAMPLE_APP_TEST_REST_PORT);

		initializeAppDeployer(new DeployRestApiServersCommand(), new DeployOtherDatabasesCommand());

		appDeployer.deploy(appConfig);

		Fragment db = dbMgr.getPropertiesAsXml(appConfig.getContentDatabaseName());
		assertTrue(db.elementExists(idRangeIndexPath));

		db = dbMgr.getPropertiesAsXml(appConfig.getTestContentDatabaseName());
		assertTrue(db.elementExists(idRangeIndexPath));
	}
}
