package com.marklogic.mgmt.api.database;

import com.marklogic.mgmt.api.database.Database;
import com.marklogic.mgmt.api.database.DatabaseSorter;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class SortDatabasesTest  {

	@Test
	public void test() {
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
}
