/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
