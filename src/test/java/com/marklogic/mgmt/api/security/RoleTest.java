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
package com.marklogic.mgmt.api.security;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class RoleTest  {

	@Test
	public void roleDependencies() {
		Role role = new Role();
		role.setRoleName("test0");
		assertTrue(role.getDependentRoleNames().isEmpty());
		assertFalse(role.hasPermissionsOrRoles());
		assertFalse(role.hasPermissionWithOwnRoleName());

		role.addRole("test1");
		role.addRole("test2");
		assertEquals(2, role.getDependentRoleNames().size());
		assertEquals("test1", role.getDependentRoleNames().get(0));
		assertEquals("test2", role.getDependentRoleNames().get(1));

		role.addPermission(new Permission("test3", "read"));
		assertEquals(3, role.getDependentRoleNames().size());

		role.addPermission(new Permission("test1", "update"));
		assertEquals(3, role.getDependentRoleNames().size());
		assertFalse(role.hasPermissionWithOwnRoleName());

		role.addPermission(new Permission("test0", "update"));
		assertEquals(4, role.getDependentRoleNames().size());
		assertTrue(role.hasPermissionWithOwnRoleName());

		role.clearPermissionsAndRoles();
		assertTrue(role.getDependentRoleNames().isEmpty());
	}
}
