package com.marklogic.rest.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class JsonNodeUtilTest  {

	private ObjectMapper mapper = ObjectMapperFactory.getObjectMapper();

	/**
	 * As of version 3.14.0, the values in a target array are now added to the source array.
	 *
	 * @throws Exception
	 */
	@Test
	public void mergeArraysWithCommonItems() throws Exception {
		String json1 = "{\"items\":[\"one\", \"two\", \"three\"]}";
		String json2 = "{\"items\":[\"two\", \"four\", \"one\"]}";

		ObjectNode node1 = (ObjectNode) mapper.readTree(json1);
		ObjectNode node2 = (ObjectNode) mapper.readTree(json2);

		ObjectNode merged = JsonNodeUtil.mergeObjectNodes(node1, node2);
		ArrayNode array = (ArrayNode) merged.get("items");
		assertEquals(4, array.size());
		assertEquals("one", array.get(0).asText());
		assertEquals("two", array.get(1).asText());
		assertEquals("three", array.get(2).asText());
		assertEquals("four", array.get(3).asText());
	}

	@Test
	public void mergeObjectsWithCommonProperties() {
		ObjectNode node1 = mapper.createObjectNode();
		node1.put("flag", true);

		ObjectNode node2 = mapper.createObjectNode();
		node2.put("flag", false);

		ObjectNode merged = JsonNodeUtil.mergeObjectNodes(node1, node2);
		assertFalse(merged.get("flag").asBoolean(), "The value from the second object should win");

		merged = JsonNodeUtil.mergeObjectNodes(node2, node1);
		assertTrue(merged.get("flag").asBoolean());
	}
}
