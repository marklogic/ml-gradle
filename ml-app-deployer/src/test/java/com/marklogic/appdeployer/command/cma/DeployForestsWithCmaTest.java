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
package com.marklogic.appdeployer.command.cma;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.mgmt.resource.forests.ForestManager;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeployForestsWithCmaTest extends AbstractAppDeployerTest {

	/**
	 * We don't really know that the forests were created via CMA, as if the ML cluster isn't 9.0-5, the call to
	 * /manage/v3 should fail silently and switch over to the forests endpoint for creating forests. The intent of this
	 * test then is just to have a focused test on creating a handful of forests with inspection of logging done manually.
	 */
	@Test
	public void test() {
		appConfig.getCmaConfig().setDeployForests(true);
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/db-only-config"));

		initializeAppDeployer(new DeployOtherDatabasesCommand(6));

		ForestManager mgr = new ForestManager(manageClient);

		try {
			deploySampleApp();
			for (int i = 1; i <= 6; i++) {
				assertTrue(mgr.exists(appConfig.getContentDatabaseName() + "-" + i));
			}
		} finally {
			undeploySampleApp();
			for (int i = 1; i <= 6; i++) {
				assertFalse(mgr.exists(appConfig.getContentDatabaseName() + "-" + i));
			}
		}
	}
}
