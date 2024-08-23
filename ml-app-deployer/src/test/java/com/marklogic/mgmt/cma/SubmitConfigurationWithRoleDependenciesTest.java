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
package com.marklogic.mgmt.cma;

import com.marklogic.mgmt.AbstractMgmtTest;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.configuration.Configuration;
import com.marklogic.mgmt.api.configuration.Configurations;
import com.marklogic.mgmt.api.security.Permission;
import com.marklogic.mgmt.api.security.Role;
import com.marklogic.mgmt.api.security.RoleObjectNodesSorter;
import com.marklogic.mgmt.resource.security.RoleManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SubmitConfigurationWithRoleDependenciesTest extends AbstractMgmtTest {

	private API api;

	@BeforeEach
	public void setup() {
		api = new API(manageClient);
	}

	@AfterEach
	public void teardown() {
		RoleManager mgr = new RoleManager(manageClient);
		mgr.deleteByIdField("sample-app-test-role1");
		mgr.deleteByIdField("sample-app-test-role2");
	}

	/**
	 * If the roles have dependencies but they're added in the correct order, then submitting them in a single
	 * config seems to work fine.
	 */
	@Test
	public void rolesInOrderOfDependencies() {
		Role r1 = new Role(api, "sample-app-test-role1");
		Role r2 = new Role(api, "sample-app-test-role2");
		r2.addRole(r1.getRoleName());

		Configuration config = new Configuration();
		config.addRole(r1.toObjectNode());
		config.addRole(r2.toObjectNode());

		new Configurations(config).submit(manageClient);

		verifyRole2DependsOnRole1();
	}

	/**
	 * If the roles have dependencies but aren't in the correct order, then the roles need to be sorted first based on
	 * their dependencies.
	 */
	@Test
	public void rolesNotInOrderOfDependencies() {
		Role r1 = new Role(api, "sample-app-test-role1");
		Role r2 = new Role(api, "sample-app-test-role2");
		r2.addRole(r1.getRoleName());

		Configuration config = new Configuration();
		config.addRole(r2.toObjectNode());
		config.addRole(r1.toObjectNode());

		config.setRoles(new RoleObjectNodesSorter().sortObjectNodes(config.getRoles()));

		new Configurations(config).submit(manageClient);

		verifyRole2DependsOnRole1();
	}

	/**
	 * This can't be created via a single config nor by two configs in the same request. Have to make separate requests
	 * as of ML 9.0-9.
	 */
	@Test
	public void roleWithPermissionDependingOnIt() {
		API api = new API(manageClient);

		Role r1 = new Role(api, "sample-app-test-role1");

		Configuration firstConfig = new Configuration();
		firstConfig.addRole(r1.toObjectNode());
		new Configurations(firstConfig).submit(manageClient);

		r1.addPermission(new Permission("sample-app-test-role1", "read"));
		Configuration secondConfig = new Configuration();
		secondConfig.addRole(r1.toObjectNode());

		new Configurations(secondConfig).submit(manageClient);

		r1 = api.role("sample-app-test-role1");
		assertNotNull(r1.getPermission(), "The role should have a permission referencing itself");
		assertEquals("sample-app-test-role1", r1.getPermission().get(0).getRoleName());
	}

	private void verifyRole2DependsOnRole1() {
		Role r2 = api.role("sample-app-test-role2");
		assertNotNull(r2.getRole());
		assertEquals("sample-app-test-role1", r2.getRole().get(0));
	}


}
