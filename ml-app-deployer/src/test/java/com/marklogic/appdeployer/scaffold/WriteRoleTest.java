/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.scaffold;

import com.marklogic.appdeployer.command.security.DeployRolesCommand;
import com.marklogic.mgmt.api.security.Role;
import com.marklogic.mgmt.template.security.RoleTemplateBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WriteRoleTest extends AbstractResourceWriterTest {

	@BeforeEach
	public void setup() {
		initializeAppDeployer(new DeployRolesCommand());
	}

	@Test
	public void test() {
		propertyMap.put("role-name", "generate-role-test");
		propertyMap.put("description", "Example description");
		propertyMap.put("role", Arrays.asList("rest-admin", "manage-admin"));

		buildResourceAndDeploy(new RoleTemplateBuilder());

		Role role = api.role("generate-role-test");
		assertEquals("Example description", role.getDescription());
		List<String> roles = role.getRole();
		assertEquals(2, roles.size());
		assertTrue(roles.contains("rest-admin"));
		assertTrue(roles.contains("manage-admin"));
	}

	@Test
	public void defaultValues() {
		buildResourceAndDeploy(new RoleTemplateBuilder());
		Role role = api.role("CHANGEME-name-of-role");
		assertEquals("CHANGEME description of role", role.getDescription());
	}
}
