/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
