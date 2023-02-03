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

		return sorter.sort();
	}
}
