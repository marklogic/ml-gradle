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
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeployDatabasesFromMultipleConfigDirsTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		List<ConfigDir> list = new ArrayList<>();
		list.add(new ConfigDir(new File("src/test/resources/sample-app/multiple-config-paths/path1")));
		list.add(new ConfigDir(new File("src/test/resources/sample-app/multiple-config-paths/path2")));
		appConfig.setConfigDirs(list);

		initializeAppDeployer(new DeployOtherDatabasesCommand());
		DatabaseManager mgr = new DatabaseManager(manageClient);
		try {
			deploySampleApp();
			assertTrue(mgr.exists("sample-app-one-database"));
			assertTrue(mgr.exists("sample-app-two-database"));
		} finally {
			undeploySampleApp();
			assertFalse(mgr.exists("sample-app-one-database"));
			assertFalse(mgr.exists("sample-app-two-database"));
		}
	}
}
