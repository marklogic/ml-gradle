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
import com.marklogic.rest.util.Fragment;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpServerErrorException;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class DeployRoleWithCapabilityQueryTest extends AbstractAppDeployerTest {

	@Test
	void jsonRole() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/qbac-json-role"));

		initializeAppDeployer(new DeployRolesCommand());

		final String message = "ML 10.0-7 is not able to deploy a JSON role with capability queries; expecting to get an XDMP-NOTQUERY error: ";
		try {
			appConfig.getCmaConfig().setDeployRoles(true);
			appDeployer.deploy(appConfig);
			fail("Expected error: " + message);
		} catch (HttpServerErrorException ex) {
			assertTrue(ex.getMessage().contains("XDMP-NOTQUERY"), message + ex.getMessage());
		}

		// Make sure error occurs with CMA disabled too
		try {
			appConfig.getCmaConfig().setDeployRoles(false);
			appDeployer.deploy(appConfig);
			fail("Expected error: " + message);
		} catch (HttpServerErrorException ex) {
			assertTrue(ex.getMessage().contains("XDMP-NOTQUERY"), message + ex.getMessage());
		}
	}

	@Test
	void xmlRole() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/qbac-xml-role"));
		initializeAppDeployer(new DeployRolesCommand());

		try {
			// This is necessary because the CMA config is JSON, and the XML capability-queries cannot be
			// converted into JSON
			appConfig.getCmaConfig().setDeployRoles(false);
			appDeployer.deploy(appConfig);

			RoleManager mgr = new RoleManager(manageClient);
			assertTrue(mgr.exists("a-qbac-xml-role"));

			Fragment props = mgr.getPropertiesAsXml("a-qbac-xml-role");
			assertEquals("hello", props.getElementValue("/m:role-properties/m:queries/m:capability-query[m:capability = 'read']" +
				"/m:query/cts:word-query/cts:text"), "Verifying that the query was saved");
			assertEquals("world", props.getElementValue("/m:role-properties/m:queries/m:capability-query[m:capability = 'update']" +
				"/m:query/cts:word-query/cts:text"), "Verifying that the query was saved");
		} finally {
			undeploySampleApp();
		}
	}

}
