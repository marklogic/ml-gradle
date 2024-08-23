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
package com.marklogic.mgmt.api.trigger;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBContext;
import java.io.StringWriter;

public class TriggerTest  {

	@Test
	public void marshal() throws Exception {
		Trigger t = new Trigger();
		t.setName("test");
		t.setDatabaseName("Triggers");

		String json = t.getJson();
		ObjectNode node = (ObjectNode) ObjectMapperFactory.getObjectMapper().readTree(json);
		assertEquals("test", node.get("name").asText());
		assertFalse(node.has("database-name") || node.has("databaseName"),
			"The databaseIdOrName property only exists to support constructing a TriggerManager");

		JAXBContext context = JAXBContext.newInstance(Trigger.class);
		StringWriter writer = new StringWriter();
		context.createMarshaller().marshal(t, writer);
		String xml = writer.toString();
		assertFalse(xml.contains("database-name") || xml.contains("databaseName"));
	}
}
