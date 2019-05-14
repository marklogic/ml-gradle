package com.marklogic.appdeployer.command.security;

import org.junit.Before;

public class DeployRolesWithDependenciesViaCmaTest extends DeployRolesWithDependenciesTest {

	@Before
	public void setup() {
		appConfig.getCmaConfig().setDeployRoles(true);
	}
}
