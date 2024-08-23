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
package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.security.Privilege;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.PrivilegeManager;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * TODO Unable to update a privilege of kind "uri".
 */
public class ManagePrivilegesTest extends AbstractManageResourceTest {

	@Override
	protected ResourceManager newResourceManager() {
		return new PrivilegeManager(manageClient);
	}

	@Override
	protected Command newCommand() {
		return new DeployPrivilegesCommand();
	}

	@Override
	protected String[] getResourceNames() {
		return new String[]{"sample-app-execute-1", "sample-app-execute-2"};
	}

	@Test
	public void privilegeWithRole() {
		appConfig.setConfigDir(new ConfigDir(new File("src/test/resources/sample-app/privileges-with-roles")));

		initializeAppDeployer(new DeployPrivilegesCommand(), new DeployRolesCommand(), new DeployPrivilegeRolesCommand());
		try {
			deploySampleApp();

			String json = new PrivilegeManager(manageClient).getPropertiesAsJson("sample-app-execute-1", "kind", "execute");
			Privilege p = new DefaultResourceMapper(new API(manageClient)).readResource(json, Privilege.class);
			assertEquals("sample-app-role1", p.getRole().get(0));
			assertEquals("manage-user", p.getRole().get(1));

			json = new PrivilegeManager(manageClient).getPropertiesAsJson("sample-app-xml-privilege", "kind", "execute");
			p = new DefaultResourceMapper(new API(manageClient)).readResource(json, Privilege.class);
			assertEquals("sample-app-role1", p.getRole().get(0));
			assertEquals("rest-admin", p.getRole().get(1));

			json = new PrivilegeManager(manageClient).getPropertiesAsJson("sample-app-execute-3", "kind", "execute");
			p = new DefaultResourceMapper(new API(manageClient)).readResource(json, Privilege.class);
			assertNull(p.getRole());
		} finally {
			undeploySampleApp();
		}
	}
}
