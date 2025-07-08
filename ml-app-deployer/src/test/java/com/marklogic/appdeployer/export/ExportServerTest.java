/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.export;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.appservers.DeployOtherServersCommand;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.groups.DeployGroupsCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.resource.groups.GroupManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.util.FileCopyUtils;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExportServerTest extends AbstractExportTest {

	@AfterEach
	public void teardown() {
		undeploySampleApp();
	}

	@Disabled("Failing due to server bug; see MLE-16059")
	@Test
	public void test() {
		appConfig.getFirstConfigDir().setBaseDir(new File("src/test/resources/sample-app/default-modules-database-config"));
		initializeAppDeployer(new DeployRestApiServersCommand(true));
		deploySampleApp();

		new Exporter(manageClient, "Default").servers("sample-app").export(exportDir);

		undeploySampleApp();

		appConfig.getFirstConfigDir().setBaseDir(exportDir);
		initializeAppDeployer(new DeployOtherServersCommand(), new DeployOtherDatabasesCommand());
		deploySampleApp();
	}

	@Test
	public void serverInOtherGroup() {
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/other-group")));
		initializeAppDeployer(new DeployGroupsCommand(), new DeployOtherServersCommand());

		final String groupName = "sample-app-other-group";
		final String serverName = "sample-app-other-server";

		deploySampleApp();
		new Exporter(manageClient, groupName).groups(groupName).serversNoDatabases(serverName).export(exportDir);
		undeploySampleApp();

		assertFalse(new GroupManager(manageClient).exists(groupName));
		assertFalse(new ServerManager(manageClient).exists(serverName));

		appConfig.getFirstConfigDir().setBaseDir(exportDir);
		deploySampleApp();

		assertTrue(new GroupManager(manageClient).exists(groupName));

		assertTrue(new ServerManager(manageClient, groupName).exists(serverName));
		assertFalse(new ServerManager(manageClient, "Default").exists(serverName));
	}
}
