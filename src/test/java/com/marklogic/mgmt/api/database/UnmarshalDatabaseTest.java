package com.marklogic.mgmt.api.database;

import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.util.ObjectMapperFactory;
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

	@Test
	public void rangePathIndexes() {
		String json = "{\"range-path-index\":[" +
			"{\"scalar-type\":\"string\", \"path-expression\":\"/firstPath\", \"collation\":\"http://marklogic.com/collation/\", \"range-value-positions\":false, \"invalid-values\":\"reject\"}, " +
			"{\"scalar-type\":\"string\", \"path-expression\":\"/secondPath\", \"collation\":\"http://marklogic.com/collation/codepoint\", \"range-value-positions\":false, \"invalid-values\":\"reject\"}" +
			"]}";

		Database d = new DefaultResourceMapper(new API(null, ObjectMapperFactory.getObjectMapper())).readResource(json, Database.class);
		assertEquals(2, d.getRangePathIndex().size());
		assertEquals("/firstPath", d.getRangePathIndex().get(0).getPathExpression());
		assertEquals("/secondPath", d.getRangePathIndex().get(1).getPathExpression());
	}
}
