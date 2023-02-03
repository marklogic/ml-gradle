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

import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class UnmarshalDatabaseTest  {

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
