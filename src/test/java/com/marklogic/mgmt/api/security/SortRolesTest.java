package com.marklogic.mgmt.api.security;

import com.fasterxml.jackson.databind.ObjectReader;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SortRolesTest extends Assert {

	private Role r1 = new Role("r1");
	private Role r2 = new Role("r2");
	private Role r3, r4;
	private List<Role> roles = new ArrayList<>();

	@Test
	public void rolesWithDependenciesWhereItMattersIfDependencyHasBeenSeenYet() {
		r3 = new Role("r3");
		r4 = new Role("r4");
		r1.addRole("app-user");
		r2.addRole("r4");
		r3.addRole("r1");
		r4.addRole("r1");
		roles.add(r1);
		roles.add(r2);
		roles.add(r3);
		roles.add(r4);

		sortRoles();

		roles.forEach(role -> System.out.println(role.getRoleName()));

		assertEquals("r1", roles.get(0).getRoleName());
		assertEquals("r4", roles.get(1).getRoleName());
		assertEquals("r3", roles.get(2).getRoleName());
		assertEquals("r2", roles.get(3).getRoleName());
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

		assertEquals(r4.getRoleName(), roles.get(0).getRoleName());
		assertEquals(r3.getRoleName(), roles.get(1).getRoleName());
		assertEquals(r2.getRoleName(), roles.get(2).getRoleName());
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

	@Test
	public void evenMoreRolesWithDependencies() throws Exception {
		File[] files = new File("src/test/resources/sample-app/even-more-roles-with-dependencies/security/roles").listFiles();
		ObjectReader reader = ObjectMapperFactory.getObjectMapper().readerFor(Role.class);
		for (File f : files) {
			roles.add(reader.readValue(f));
		}

		sortRoles();

		assertEquals("xyz-reader", roles.get(0).getRoleName());
		assertEquals("xyz-writer", roles.get(1).getRoleName());
		assertEquals("xyz-admin", roles.get(2).getRoleName());
		assertEquals("abc-sss-ui-role", roles.get(3).getRoleName());
		assertEquals("abc-ui-offline-user", roles.get(4).getRoleName());
		assertEquals("abc-ui-offline-admin", roles.get(5).getRoleName());
		assertEquals("Doesn't matter when this is created because it has no dependencies",
			"abc-ui-developer", roles.get(6).getRoleName());
		assertEquals("abc-ui-admin", roles.get(7).getRoleName());
		assertEquals("Doesn't matter when this is created because it has no dependencies",
			"abc-login-role", roles.get(8).getRoleName());
	}

	private void sortRoles() {
		roles = new RoleObjectNodesSorter().sortRoles(roles);
	}
}
