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
import com.marklogic.appdeployer.command.security.DeployPrivilegesCommand;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.PrivilegeManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeployPrivilegesWithCmaTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		initializeAppDeployer(new TestDeployPrivilegesCommand());

		PrivilegeManager mgr = new PrivilegeManager(manageClient);
		appConfig.getCmaConfig().setDeployPrivileges(true);

		try {
			deploySampleApp();
			assertTrue(mgr.exists("sample-app-execute-1"));
			assertTrue(mgr.exists("sample-app-execute-2"));

			deploySampleApp();
			assertTrue(mgr.exists("sample-app-execute-1"));
			assertTrue(mgr.exists("sample-app-execute-2"));
		} finally {
			initializeAppDeployer(new DeployPrivilegesCommand());

			undeploySampleApp();
			assertFalse(mgr.exists("sample-app-execute-1"));
			assertFalse(mgr.exists("sample-app-execute-2"));
		}
	}
}

class TestDeployPrivilegesCommand extends DeployPrivilegesCommand {
	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		// Returning null to force an error in case this is used at all
		return null;
	}
}
