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
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.security.DeployProtectedPathsCommand;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.ProtectedPathManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeployProtectedPathsWithCmaTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		initializeAppDeployer(new TestDeployProtectedPathsCommand());

		ProtectedPathManager mgr = new ProtectedPathManager(manageClient);
		appConfig.getCmaConfig().setDeployProtectedPaths(true);

		try {
			deploySampleApp();
			assertTrue(mgr.exists("/test:element"));

			deploySampleApp();
			assertTrue(mgr.exists("/test:element"));
		} finally {
			initializeAppDeployer(new DeployProtectedPathsCommand());

			undeploySampleApp();
			assertFalse(mgr.exists("/test:element"));
		}
	}
}

class TestDeployProtectedPathsCommand extends DeployProtectedPathsCommand {
	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		// Returning null to force an error in case this is used at all
		return null;
	}
}
