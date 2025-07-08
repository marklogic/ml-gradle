/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.mgmt.resource.security.RoleManager;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeployRoleThatRefersToItselfTest extends AbstractAppDeployerTest {

	/**
	 * Tests both an XML file and a JSON file so we can verify that each type can be deserialized correctly.
	 */
	@Test
	public void test() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/role-refers-to-itself"));
		initializeAppDeployer(new DeployRolesCommand());

		try {
			appDeployer.deploy(appConfig);

			RoleManager mgr = new RoleManager(manageClient);
			assertTrue(mgr.exists("sample-app-json-role"));
			assertTrue(mgr.exists("sample-app-xml-role"));

		} finally {
			undeploySampleApp();
		}
	}

}
