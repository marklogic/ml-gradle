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
		List<Role> roles = new ArrayList<>();
		ObjectReader reader = ObjectMapperFactory.getObjectMapper().readerFor(Role.class);
		for (ObjectNode node : objectNodes) {
			try {
				roles.add(reader.readValue(node));
			} catch (IOException e) {
				throw new RuntimeException("Unable to read ObjectNode into Role; node: " + node, e);
			}
		}

		roles = sortRoles(roles);

		List<ObjectNode> newList = new ArrayList<>();
		roles.forEach(role -> newList.add(role.toObjectNode()));
		return newList;
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

		String[] sortedRoleNames = sorter.sort();
		roles = new ArrayList<>();
		for (String name : sortedRoleNames) {
			roles.add(map.get(name));
		}
		return roles;
	}
}
