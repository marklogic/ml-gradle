package com.marklogic.mgmt.api.database;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CompareDatabasesTest extends Assert {

	private Database db1 = new Database();
	private Database db2 = new Database();

	@Before
	public void setup() {
		db1.setDatabaseName("first");
		db2.setDatabaseName("second");
	}

	@Test
	public void noDependencies() {
		verify(0, 0);
	}

	@Test
	public void schemaDependency() {
		db1.setSchemaDatabase("second");
		verify(1, -1);
	}

	@Test
	public void triggersDependency() {
		db1.setTriggersDatabase("second");
		verify(1, -1);
	}

	@Test
	public void securityDependency() {
		db1.setSecurityDatabase("second");
		verify(1, -1);
	}

	/**
	 * 1 is expected instead of zero so that the databases aren't left in place when they really ought to be sorted
	 * further - i.e. a long string of databases with dependencies but not on each other won't be moved at all, which
	 * may prevent a database from being compared to something it does have dependencies on (not 100% sure this can
	 * happen, but it was the bug that led to pull request #344 in ml-app-deployer).
	 */
	@Test
	public void bothHaveDependencies() {
		db1.setSchemaDatabase("other");
		db2.setTriggersDatabase("another");
		verify(1, 1);
	}

	private void verify(int firstComparison, int secondComparison) {
		assertEquals(firstComparison, db1.compareTo(db2));
		assertEquals(secondComparison, db2.compareTo(db1));
	}
}
