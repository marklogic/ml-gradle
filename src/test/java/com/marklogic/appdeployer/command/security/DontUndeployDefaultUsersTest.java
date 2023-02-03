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
package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.resource.security.UserManager;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DontUndeployDefaultUsersTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/users-to-not-undeploy"));
		initializeAppDeployer(new DeployUsersCommand());

		UserManager mgr = new UserManager(manageClient);
		assertFalse(mgr.exists("ml-app-deployer-test-user"));
		assertTrue(mgr.exists("nobody"));

		deploySampleApp();

		try {
			assertTrue(mgr.exists("ml-app-deployer-test-user"));
			assertTrue(mgr.exists("nobody"));
		} finally {
			undeploySampleApp();

			assertFalse(mgr.exists("ml-app-deployer-test-user"));
			assertTrue(mgr.exists("nobody"), "The 'nobody' user should not have been deleted since it's in the list of " +
				"users to not undeploy");
		}
	}

	@Test
	public void verifySetOfDefaultUsers() {
		// Current as of ML 9.0-9.1
		Set<String> users = new DeployUsersCommand().getDefaultUsersToNotUndeploy();
		assertEquals(4, users.size());
		assertTrue(users.contains("admin"));
		assertTrue(users.contains("healthcheck"));
		assertTrue(users.contains("infostudio-admin"));
		assertTrue(users.contains("nobody"));
	}
}
