package com.marklogic.appdeployer.command.security;

import org.junit.jupiter.api.BeforeEach;

public class DeployRolesWithDependenciesViaCmaTest extends DeployRolesWithDependenciesTest {

	@BeforeEach
	public void setup() {
		appConfig.getCmaConfig().setDeployRoles(true);
	}
}
