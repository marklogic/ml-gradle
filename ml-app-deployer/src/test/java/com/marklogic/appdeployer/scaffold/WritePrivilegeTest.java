/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
