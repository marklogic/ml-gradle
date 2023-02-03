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
package com.marklogic.appdeployer.impl;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

import static org.springframework.test.util.AssertionErrors.fail;

public class IgnoreCommandErrorTest extends AbstractAppDeployerTest {

	@Test
	public void ignoreErrorOnUndeploy() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/db-only-config"));

		initializeAppDeployer(new DeployRestApiServersCommand(true), new DeployOtherDatabasesCommand(1));
		deploySampleApp();

		try {
			try {
				initializeAppDeployer(new DeployOtherDatabasesCommand(1));
				appDeployer.undeploy(appConfig);
				fail("Undeploying should have failed because the database is attached to the REST server");
			} catch (RuntimeException ex) {
				logger.info("Caught expected exception: " + ex.getMessage());
			}

			appConfig.setCatchUndeployExceptions(true);
			// This should just log the error and not throw one
			appDeployer.undeploy(appConfig);
		} finally {
			initializeAppDeployer(new DeployRestApiServersCommand(true), new DeployOtherDatabasesCommand());
			appDeployer.undeploy(appConfig);
		}
	}

	@Test
	public void ignoreErrorOnDeploy() {
		appConfig.setResourceFilenamesIncludePattern(Pattern.compile("content.*.json"));
		initializeAppDeployer(new DeployOtherDatabasesCommand(1));

		try {
			deploySampleApp();
			fail("Deploying should have failed because the content database refers to a non-existent schemas database");
		} catch (Exception ex) {
			logger.info("Caught expected exception: " + ex.getMessage());
		}

		appConfig.setCatchDeployExceptions(true);
		// The error should just be logged
		deploySampleApp();

		// Nothing to undeploy!
	}
}
