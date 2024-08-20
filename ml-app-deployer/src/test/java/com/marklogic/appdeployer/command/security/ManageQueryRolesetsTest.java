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

import com.marklogic.appdeployer.command.AbstractManageResourceTest;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.security.queryroleset.QueryRoleset;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.QueryRolesetManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ManageQueryRolesetsTest extends AbstractManageResourceTest {

	@Override
	protected ResourceManager newResourceManager() {
		return new QueryRolesetManager(manageClient);
	}

	@Override
	protected Command newCommand() {
		return new DeployQueryRolesetsCommand();
	}

	@Override
	protected String[] getResourceNames() {
		return new String[]{};
	}

	@Override
	protected void afterResourcesCreated() {
		API api = new API(manageClient);
		QueryRoleset qr = api.queryRoleset("view-admin", "flexrep-user");
		assertEquals("view-admin", qr.getRoleName().get(0));
		assertEquals("flexrep-user", qr.getRoleName().get(1));

		// Verify that XML unmarshalling works
		qr = new DefaultResourceMapper(api).readResource(
			new QueryRolesetManager(manageClient).getPropertiesAsXmlString(qr.getRoleNamesAsJsonArrayString()),
			QueryRoleset.class
		);
		assertEquals("view-admin", qr.getRoleName().get(0));
		assertEquals("flexrep-user", qr.getRoleName().get(1));
	}
}
