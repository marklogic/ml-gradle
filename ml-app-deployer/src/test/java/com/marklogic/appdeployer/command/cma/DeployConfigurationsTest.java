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
import com.marklogic.mgmt.resource.forests.ForestManager;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeployConfigurationsTest extends AbstractAppDeployerTest {

	@Test
	public void deployJsonConfig() {
		setConfigBaseDir("sample-app/cma");
		appConfig.setResourceFilenamesIncludePattern(Pattern.compile(".*json"));
		initializeAppDeployer(new DeployConfigurationsCommand());
		deploySampleApp();

		verifyForestsWereCreated();
	}

	@Test
	public void deployXmlConfig() {
		setConfigBaseDir("sample-app/cma");
		appConfig.setResourceFilenamesIncludePattern(Pattern.compile(".*xml"));
		initializeAppDeployer(new DeployConfigurationsCommand());
		deploySampleApp();

		verifyForestsWereCreated();
	}

	private void verifyForestsWereCreated() {
		ForestManager forestManager = new ForestManager(manageClient);
		final String[] configForestNames = new String[]{"cma-test-f1", "cma-test-f2", "cma-test-f3"};
		try {
			for (String name : configForestNames) {
				assertTrue(forestManager.exists(name));
			}
		} finally {
			for (String name : configForestNames) {
				if (forestManager.exists(name)) {
					forestManager.delete(name, ForestManager.DELETE_LEVEL_FULL);
				}
			}
		}
	}
}
