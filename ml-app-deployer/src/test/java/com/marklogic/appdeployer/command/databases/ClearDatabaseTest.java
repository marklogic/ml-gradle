/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.client.DatabaseClient;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class ClearDatabaseTest extends AbstractAppDeployerTest {

	@AfterEach
	public void teardown() {
		undeploySampleApp();
	}

	/**
	 * Testing against the modules database, but the operation is the same regardless of database.
	 * <p>
	 * Also, for the modules database, ml-gradle 1.+ had the ability to exclude certain modules from being deleted in
	 * case those modules were needed by the REST API rewriter. But in that case, it's usually preferable to just use
	 * the XCC approach for loading asset modules.
	 */
	@Test
	void modulesDatabase() {
		initializeAppDeployer(new DeployRestApiServersCommand(true), buildLoadModulesCommand());

		appConfig.setRestPort(8004);
		appDeployer.deploy(appConfig);

		DatabaseManager mgr = new DatabaseManager(manageClient);
		mgr.clearDatabase(appConfig.getModulesDatabaseName());

		try (DatabaseClient client = newDatabaseClient("sample-app-modules")) {
			String uris = client.newServerEval().xquery("cts:uris((), (), cts:and-query(()))").evalAs(String.class);
			assertNull(uris, "The modules database should have been cleared.");
		}
	}
}
