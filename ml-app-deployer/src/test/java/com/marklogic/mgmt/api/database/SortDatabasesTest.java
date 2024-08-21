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

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SortDatabasesTest {

	@Test
	void test() {
		Database db1 = new Database(null, "db1");
		Database db2 = new Database(null, "db2");
		Database triggersDb = new Database(null, "triggers-db");

		db1.setTriggersDatabase(triggersDb.getDatabaseName());

		List<Database> list = Arrays.asList(db1, db2, triggersDb);
		list.forEach(db -> {
			db.setSchemaDatabase("Schemas");
			db.setSecurityDatabase("Security");
		});

		String[] sortedNames = new DatabaseSorter().sortDatabasesAndReturnNames(list);
		assertEquals("triggers-db", sortedNames[0]);
		assertEquals("db2", sortedNames[1]);
		assertEquals("db1", sortedNames[2]);
	}

	@Test
	void circularDependencies() {
		Database db1 = new Database(null, "db1");
		Database db2 = new Database(null, "db2");
		db1.setTriggersDatabase(db2.getDatabaseName());
		db2.setSchemaDatabase(db1.getDatabaseName());
		List<Database> databases = Arrays.asList(db1, db2);

		DatabaseSorter sorter = new DatabaseSorter();
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
			() -> sorter.sortDatabasesAndReturnNames(databases));

		assertTrue(ex.getMessage().startsWith("Unable to deploy databases due to circular dependencies between " +
				"two or more databases; please remove these circular dependencies in order to deploy your databases."),
			"Unexpected error message: " + ex.getMessage());
	}
}
