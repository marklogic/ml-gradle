package com.marklogic.mgmt.api.security;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompareRolesTest extends Assert {

	private Role r1 = new Role("r1");
	private Role r2 = new Role("r2");
	private Role r3, r4;
	private List<Role> roles = new ArrayList<>();

	@Test
	public void noDependencies() {
		assertEquals(-1, r1.compareTo(r2));
		assertEquals(1, r2.compareTo(r1));
	}

	@Test
	public void roleDependency() {
		r1.addRole(r2.getRoleName());
		assertEquals(1, r1.compareTo(r2));
		assertEquals(-1, r2.compareTo(r1));
	}

	@Test
	public void permissionDependency() {
		r1.addPermission(new Permission(r2.getRoleName(), "read"));
		assertEquals(1, r1.compareTo(r2));
		assertEquals(-1, r2.compareTo(r1));
	}

	@Test
	public void sortList() {
		r3 = new Role("r3");
		r4 = new Role("r4");
		r1.addRole(r4.getRoleName());

		roles.add(r1);
		roles.add(r2);
		roles.add(r3);
		roles.add(r4);

		sortRoles();

		assertEquals(r2.getRoleName(), roles.get(0).getRoleName());
		assertEquals(r3.getRoleName(), roles.get(1).getRoleName());
		assertEquals(r4.getRoleName(), roles.get(2).getRoleName());
		assertEquals(r1.getRoleName(), roles.get(3).getRoleName());
	}

	@Test
	public void sortListWithManyRoleDependencies() {
		r3 = new Role("r3");
		r4 = new Role("r4");

		r2.addRole(r1.getRoleName());
		r4.addRole(r1.getRoleName());
		r3.addRole(r2.getRoleName());

		roles.add(r4);
		roles.add(r3);
		roles.add(r1);
		roles.add(r2);

		sortRoles();

		assertEquals(r1.getRoleName(), roles.get(0).getRoleName());
		assertEquals(r2.getRoleName(), roles.get(1).getRoleName());
		assertEquals("r3 is here because it has no relation to r4, so their names should be compared",
			r3.getRoleName(), roles.get(2).getRoleName());
		assertEquals(r4.getRoleName(), roles.get(3).getRoleName());
	}

	private void sortRoles() {
		Collections.sort(roles, (role1, role2) -> {
			int result = role1.compareTo(role2);
			//System.out.println(role1.getRoleName() + ":" + role2.getRoleName() + ": " + result);
			return result;
		});
	}
}
