/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeployDatabasesFromMultipleConfigDirsTest extends AbstractAppDeployerTest {

	@Test
	public void test() {
		List<ConfigDir> list = new ArrayList<>();
		list.add(new ConfigDir(new File("src/test/resources/sample-app/multiple-config-paths/path1")));
		list.add(new ConfigDir(new File("src/test/resources/sample-app/multiple-config-paths/path2")));
		appConfig.setConfigDirs(list);

		initializeAppDeployer(new DeployOtherDatabasesCommand());
		DatabaseManager mgr = new DatabaseManager(manageClient);
		try {
			deploySampleApp();
			assertTrue(mgr.exists("sample-app-one-database"));
			assertTrue(mgr.exists("sample-app-two-database"));
		} finally {
			undeploySampleApp();
			assertFalse(mgr.exists("sample-app-one-database"));
			assertFalse(mgr.exists("sample-app-two-database"));
		}
	}
}
