package com.marklogic.rest.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import com.marklogic.rest.util.JsonNodeUtil;
import org.junit.Assert;
import org.junit.Test;

public class JsonNodeUtilTest extends Assert {

	private ObjectMapper mapper = ObjectMapperFactory.getObjectMapper();

	@Test
	public void mergeArraysWithCommonItems() throws Exception {
		String json1 = "{\"items\":[\"one\", \"two\", \"three\"]}";
		String json2 = "{\"items\":[\"two\", \"four\", \"one\"]}";

		ObjectNode node1 = (ObjectNode) mapper.readTree(json1);
		ObjectNode node2 = (ObjectNode) mapper.readTree(json2);

		ObjectNode merged = JsonNodeUtil.mergeObjectNodes(node1, node2);
		ArrayNode array = (ArrayNode) merged.get("items");
		assertEquals(4, array.size());
		assertEquals("two", array.get(0).asText());
		assertEquals("four", array.get(1).asText());
		assertEquals("one", array.get(2).asText());
		assertEquals("three", array.get(3).asText());
	}
}
