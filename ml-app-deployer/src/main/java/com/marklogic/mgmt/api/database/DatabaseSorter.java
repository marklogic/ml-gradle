/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.database;

import com.marklogic.mgmt.util.TopologicalSorter;

import java.util.ArrayList;
import java.util.List;

public class DatabaseSorter {

	public String[] sortDatabasesAndReturnNames(List<Database> databases) {
		final int size = databases.size();
		TopologicalSorter sorter = new TopologicalSorter(size);
		List<String> dbNames = new ArrayList<>();

		databases.forEach(db -> {
			sorter.addVertex(db.getDatabaseName());
			dbNames.add(db.getDatabaseName());
		});

		for (int i = 0; i < size; i++) {
			for (String dependency : databases.get(i).getDatabaseDependencyNames()) {
				int index = dbNames.indexOf(dependency);
				// If the dependency is not in the list of databases, it must already exist, and thus we don't need
				// to worry about it
				if (index > -1) {
					sorter.addEdge(index, i);
				}
			}
		}

		try {
			return sorter.sort();
		} catch (IllegalStateException ex) {
			throw new IllegalArgumentException("Unable to deploy databases due to circular dependencies " +
				"between two or more databases; please remove these circular dependencies in order to deploy" +
				" your databases. An example of a circular dependency is database A depending on database B as its " +
				"triggers databases, while database B depends on database A as its schemas database.");
		}
	}
}
