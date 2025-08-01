/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.security;

import org.junit.jupiter.api.BeforeEach;

public class DeployRolesWithDependenciesViaCmaTest extends DeployRolesWithDependenciesTest {

	@BeforeEach
	public void setup() {
		appConfig.getCmaConfig().setDeployRoles(true);
	}
}
