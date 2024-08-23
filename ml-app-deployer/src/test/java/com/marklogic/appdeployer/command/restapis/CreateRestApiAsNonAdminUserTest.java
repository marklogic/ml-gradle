/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.appdeployer.command.restapis;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.security.DeployRolesCommand;
import com.marklogic.appdeployer.command.security.DeployUsersCommand;
import com.marklogic.junit.BaseTestHelper;
import com.marklogic.junit.PermissionsFragment;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.xcc.template.XccTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

public class CreateRestApiAsNonAdminUserTest extends AbstractAppDeployerTest {

	@Autowired
	private ManageConfig manageConfig;

	private XccTemplate xccTemplate;

	@BeforeEach
	public void setup() {
		xccTemplate = newModulesXccTemplate();
	}

	@AfterEach
	public void teardown() {
		undeploySampleApp();
	}

	@Test
	public void test() {
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
		PermissionsFragment perms = new BaseTestHelper().getDocumentPermissions("/ext/hello-lib.xqy", xccTemplate);
		perms.assertPermissionExists("rest-admin", "read");
		perms.assertPermissionExists("rest-admin", "update");
		perms.assertPermissionExists("rest-extension-user", "execute");
	}
}
