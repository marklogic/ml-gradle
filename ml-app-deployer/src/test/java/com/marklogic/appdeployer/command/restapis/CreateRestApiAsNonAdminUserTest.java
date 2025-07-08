/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.restapis;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.security.DeployRolesCommand;
import com.marklogic.appdeployer.command.security.DeployUsersCommand;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit5.PermissionsTester;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

class CreateRestApiAsNonAdminUserTest extends AbstractAppDeployerTest {

	@Autowired
	private ManageConfig manageConfig;

	@AfterEach
	public void teardown() {
		undeploySampleApp();
	}

	@Test
	void test() {
		// Use config specific to this test
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/non-admin-test/ml-config")));
		appConfig.getModulePaths().clear();
		appConfig.getModulePaths().add("src/test/resources/non-admin-test/ml-modules");

		// Now rebuild ManageClient using a ManageConfig that doesn't require the admin user
		ManageConfig newConfig = new ManageConfig(manageConfig.getHost(), manageConfig.getPort(),
			"sample-app-manage-admin", "sample-app-manage-admin");
		// Need to set this to the admin user so that user is used to create our app-specific users/roles/privileges
		newConfig.setSecurityUsername(manageConfig.getUsername());
		newConfig.setSecurityPassword(manageConfig.getPassword());
		this.manageClient = new ManageClient(newConfig);

		// And ensure we use our custom user for loading modules; the custom app role has the privileges required for
		// inserting modules via the REST API
		appConfig.setRestAdminUsername("sample-app-rest-admin");
		appConfig.setRestAdminPassword("sample-app-rest-admin");

		initializeAppDeployer(new DeployRestApiServersCommand(true), new DeployRolesCommand(), new DeployUsersCommand(),
			buildLoadModulesCommand());
		appDeployer.deploy(appConfig);

		// And now ensure that the module was loaded correctly
		try (DatabaseClient client = newDatabaseClient("sample-app-modules")) {
			XMLDocumentManager mgr = client.newXMLDocumentManager();
			DocumentMetadataHandle metadata = new DocumentMetadataHandle();
			mgr.read("/ext/hello-lib.xqy", metadata, new StringHandle());
			PermissionsTester perms = new PermissionsTester(metadata.getPermissions());

			perms.assertReadPermissionExists("rest-admin");
			perms.assertUpdatePermissionExists("rest-admin");
			perms.assertExecutePermissionExists("rest-extension-user");
		}
	}
}
