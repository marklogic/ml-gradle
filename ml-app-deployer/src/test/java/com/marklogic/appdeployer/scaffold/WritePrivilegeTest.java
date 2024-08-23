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
package com.marklogic.appdeployer.scaffold;

import com.marklogic.appdeployer.command.security.DeployPrivilegeRolesCommand;
import com.marklogic.appdeployer.command.security.DeployPrivilegesCommand;
import com.marklogic.mgmt.api.security.Privilege;
import com.marklogic.mgmt.template.security.PrivilegeTemplateBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WritePrivilegeTest extends AbstractResourceWriterTest {

	@Test
	public void defaultValues() {
		initializeAppDeployer(new DeployPrivilegesCommand(), new DeployPrivilegeRolesCommand());
		buildResourceAndDeploy(new PrivilegeTemplateBuilder());

		Privilege p = api.privilegeExecute("CHANGEME-name-of-privilege");
		assertEquals("CHANGEME", p.getAction());
		assertEquals("execute", p.getKind());

		List<String> roles = p.getRole();
		assertEquals(2, roles.size());
		assertTrue(roles.contains("rest-reader"));
		assertTrue(roles.contains("rest-writer"));
	}

	@Test
	public void uriPrivilege() {
		initializeAppDeployer(new DeployPrivilegesCommand());

		propertyMap.put("privilege-name", "CHANGEME-uri-privilege");
		propertyMap.put("action", "test");
		propertyMap.put("kind", "uri");
		propertyMap.put("role", null);
		buildResourceAndDeploy(new PrivilegeTemplateBuilder());

		Privilege p = api.privilegeUri("CHANGEME-uri-privilege");
		// Odd, the Manage API automatically appends a "/"
		assertEquals("test/", p.getAction());
		assertEquals("uri", p.getKind());
		assertNull(p.getRole());
	}
}
