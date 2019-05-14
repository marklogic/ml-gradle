package com.marklogic.mgmt.api.security;

import org.junit.Assert;
import org.junit.Test;

public class RoleTest extends Assert {

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
