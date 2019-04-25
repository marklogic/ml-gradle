package com.marklogic.mgmt.api.trigger;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import java.io.StringWriter;

public class TriggerTest extends Assert {

	@Test
	public void marshal() throws Exception {
		Trigger t = new Trigger();
		t.setName("test");
		t.setDatabaseName("Triggers");

		String json = t.getJson();
		ObjectNode node = (ObjectNode) ObjectMapperFactory.getObjectMapper().readTree(json);
		assertEquals("test", node.get("name").asText());
		assertFalse("The databaseIdOrName property only exists to support constructing a TriggerManager",
			node.has("database-name") || node.has("databaseName"));

		JAXBContext context = JAXBContext.newInstance(Trigger.class);
		StringWriter writer = new StringWriter();
		context.createMarshaller().marshal(t, writer);
		String xml = writer.toString();
		assertFalse(xml.contains("database-name") || xml.contains("databaseName"));
	}
}
