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
import org.junit.jupiter.api.Test;

import java.io.File;

public class CreateAndUpdateDatabaseTest extends AbstractAppDeployerTest {

	/**
	 * TODO Update this test to perform the second deployment as a user that only has privileges to update indexes.
	 */
	@Test
	public void test() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/db-only-config"));

		initializeAppDeployer(new DeployOtherDatabasesCommand(1));

		try {
			deploySampleApp();
			deploySampleApp();
		} finally {
			undeploySampleApp();
		}
	}
}
