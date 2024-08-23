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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import com.marklogic.mgmt.util.ObjectNodesSorter;
import com.marklogic.mgmt.util.TopologicalSorter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleObjectNodesSorter implements ObjectNodesSorter {

	/**
	 * Return a new list of object nodes, sorted based on the dependencies of each role.
	 *
	 * @param objectNodes
	 * @return
	 */
	@Override
	public List<ObjectNode> sortObjectNodes(List<ObjectNode> objectNodes) {
		// Construct a list of roles so they can be sorted
		List<Role> roles = new ArrayList<>();

		// Construct a map of roles to object nodes so that the original object nodes can be added to the list.
		// This is to resolve bug #441, where capability-query's are being dropped because the Role class doesn't
		// support them. It may be better to refactor this to not deserialize into Role instances so that ObjectNodes
		// are used the entire time, even though we'd lose some of the convenience methods provided by the Role class.
		final Map<String, ObjectNode> roleMap = new HashMap();

		ObjectReader reader = ObjectMapperFactory.getObjectMapper().readerFor(Role.class);
		for (ObjectNode objectNode : objectNodes) {
			try {
				Role role = reader.readValue(objectNode);
				roles.add(role);
				roleMap.put(role.getRoleName(), objectNode);
			} catch (IOException e) {
				throw new RuntimeException("Unable to read ObjectNode into Role; JSON: " + objectNode, e);
			}
		}

		roles = sortRoles(roles);

		List<ObjectNode> sortedNodes = new ArrayList<>();
		roles.forEach(role -> {
			// Sanity check; we don't ever expect to get back a role in the sorted list that isn't in the roleMap
			if (roleMap.containsKey(role.getRoleName())) {
				sortedNodes.add(roleMap.get(role.getRoleName()));
			}
		});
		return sortedNodes;
	}


	/**
	 * Performs a topological sort to sort the list of roles. Ignores two kinds of dependencies - a dependency on a
	 * role that's not in the list (such a role is assumed to exist in MarkLogic already), and a dependency on the
	 * role itself. The latter is assumed to be addressed by creating the role without any dependencies first.
	 * <p>
	 * Public so that a client can use it when they have a list of Role objects instead of ObjectNode objects.
	 *
	 * @param roles
	 * @return
	 */
	public List<Role> sortRoles(List<Role> roles) {
		final int count = roles.size();
		final TopologicalSorter sorter = new TopologicalSorter(count);
		final List<String> roleNames = new ArrayList<>();
		final Map<String, Role> map = new HashMap<>();

		roles.forEach(role -> {
			sorter.addVertex(role.getRoleName());
			roleNames.add(role.getRoleName());
			map.put(role.getRoleName(), role);
		});

		for (int i = 0; i < count; i++) {
			for (String dependency : roles.get(i).getDependentRoleNames()) {
				int index = roleNames.indexOf(dependency);
				// If the role has a dependency on itself, ignore it. It is assumed that the role will first be
				// created without any roles or permissions.
				if (index == i) {
					continue;
				}
				// If it's not in the list of roles to create, it must already exist, and thus we don't need to
				// worry about it
				else if (index > -1) {
					sorter.addEdge(index, i);
				}
			}
		}

		String[] sortedRoleNames;
		try {
			sortedRoleNames = sorter.sort();
		} catch (IllegalStateException ex) {
			throw new IllegalArgumentException("Unable to deploy roles due to circular dependencies " +
				"between two or more roles; please remove these circular dependencies in order to deploy" +
				" your roles");
		}

		roles = new ArrayList<>();
		for (String name : sortedRoleNames) {
			roles.add(map.get(name));
		}
		return roles;
	}
}
