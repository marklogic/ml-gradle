package com.marklogic.mgmt.api.database;

import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import org.junit.Assert;
import org.junit.Test;

public class UnmarshalDatabaseTest extends Assert {

	@Test
	public void xmlSmokeTest() {
		String xml = "<database-properties xmlns=\"http://marklogic.com/manage/database/properties\">" +
			"<database-name>my-name</database-name>" +
			"<triggers-database>my-triggers</triggers-database>" +
			"<schema-database>my-schemas</schema-database>" +
			"</database-properties>";

		Database d = new DefaultResourceMapper().readResource(xml, Database.class);
		assertEquals("my-name", d.getDatabaseName());
		assertEquals("my-triggers", d.getTriggersDatabase());
		assertEquals("my-schemas", d.getSchemaDatabase());
	}
}
