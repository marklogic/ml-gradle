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

import com.fasterxml.jackson.databind.ObjectReader;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SortRolesTest  {

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
		assertEquals(r3.getRoleName(), roles.get(2).getRoleName(),
			"r3 is here because it has no relation to r4, so their names should be compared");
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

		List<String> roleNames = new ArrayList<>();
		for (Role role : roles) {
			roleNames.add(role.getRoleName());
		}

		assertEquals("xyz-reader", roles.get(0).getRoleName());
		assertEquals("xyz-writer", roles.get(1).getRoleName());

		// The exact positions of these don't matter, but rather the order does
		assertTrue(roleNames.indexOf("abc-ui-offline-user") > roleNames.indexOf("abc-sss-ui-role"));
		assertTrue(roleNames.indexOf("abc-ui-offline-admin") > roleNames.indexOf("abc-ui-offline-user"));
		assertTrue(roleNames.indexOf("abc-ui-admin") > roleNames.indexOf("abc-ui-offline-admin"));
	}

	private void sortRoles() {
		roles = new RoleObjectNodesSorter().sortRoles(roles);
	}
}
