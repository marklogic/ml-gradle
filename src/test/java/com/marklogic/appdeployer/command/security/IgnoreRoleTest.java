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
import com.marklogic.mgmt.resource.security.RoleManager;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class IgnoreRoleTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		appConfig.setResourceFilenamesToIgnore("sample-app-role2.xml");
		initializeAppDeployer(new DeployRolesCommand());
		appDeployer.deploy(appConfig);

		try {
			RoleManager mgr = new RoleManager(manageClient);
			assertTrue(mgr.exists("sample-app-role1"));
			assertFalse(mgr.exists("sample-app-role2"), "Role should not have been created because its resource file was ignored");
		} finally {
			undeploySampleApp();
		}
	}

	@Test
	public void excludeRoles() {
		appConfig.setResourceFilenamesExcludePattern(Pattern.compile(".*role1.*"));
		initializeAppDeployer(new DeployRolesCommand());
		appDeployer.deploy(appConfig);

		try {
			RoleManager mgr = new RoleManager(manageClient);
			assertTrue(mgr.exists("sample-app-role2"));
			assertFalse(mgr.exists("sample-app-role1"), "Role should not have been created because its filename was excluded via regex");
		} finally {
			undeploySampleApp();
		}
	}

	@Test
	public void includeRoles() {
		appConfig.setResourceFilenamesIncludePattern(Pattern.compile(".*role1.*"));
		initializeAppDeployer(new DeployRolesCommand());
		appDeployer.deploy(appConfig);

		try {
			RoleManager mgr = new RoleManager(manageClient);
			assertTrue(mgr.exists("sample-app-role1"));
			assertFalse(mgr.exists("sample-app-role2"), "Role should not have been created because its filename was not included via regex");
		} finally {
			undeploySampleApp();
		}
	}

	@Test
	public void includeAndExcludeRoles() {
		appConfig.setResourceFilenamesExcludePattern(Pattern.compile(".*role2.*"));
		appConfig.setResourceFilenamesIncludePattern(Pattern.compile(".*role1.*"));
		initializeAppDeployer(new DeployRolesCommand());
		try {
			appDeployer.deploy(appConfig);
			fail("Deployment should have failed because exclude and include patterns can't both be set");
		} catch (Exception ex) {
			assertEquals("Both excludePattern and includePattern cannot be specified", ex.getMessage());
		}
	}
}
