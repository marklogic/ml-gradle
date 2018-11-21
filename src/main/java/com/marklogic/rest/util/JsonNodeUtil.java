package com.marklogic.rest.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.mgmt.util.ObjectMapperFactory;

import java.io.File;
import java.util.*;

public class JsonNodeUtil {

	public static JsonNode mergeJsonFiles(List<File> files) {
		List<JsonNode> nodes = new ArrayList<>();
		for (File f : files) {
			if (f.exists()) {
				ObjectNode node = null;
				try {
					node = (ObjectNode) ObjectMapperFactory.getObjectMapper().readTree(f);
				} catch (Exception e) {
					throw new RuntimeException("Unable to read JSON from file: " + f.getAbsolutePath(), e);
				}
				nodes.add(node);
			}
		}

		if (nodes.isEmpty()) {
			return null;
		}

		return mergeObjectNodes(nodes.toArray(new ObjectNode[]{}));
	}

	public static ObjectNode mergeObjectNodes(ObjectNode... nodes) {
		List<ObjectNode> nodeList = new ArrayList<>();
		Set<String> fieldNames = new HashSet<>();
		for (ObjectNode node : nodes) {
			nodeList.add(node);
			Iterator<String> names = node.fieldNames();
			while (names.hasNext()) {
				fieldNames.add(names.next());
			}
		}

		if (nodeList.isEmpty()) {
			return null;
		}

		// Merge each node back into the previous one
		for (int i = 1; i < nodeList.size(); i++) {
			ObjectNode target = nodeList.get(i);
			ObjectNode source = nodeList.get(i - 1);
			for (String name : fieldNames) {
				JsonNode targetField = target.get(name);
				JsonNode sourceField = source.get(name);
				if (sourceField == null) {
					continue;
				} else if (targetField == null) {
					target.set(name, sourceField);
				} else if (sourceField.isArray()) {
					ArrayNode sourceArray = (ArrayNode) sourceField;
					ArrayNode targetArray = (ArrayNode) targetField;
					targetArray.addAll(sourceArray);
				}
			}
		}

		return nodeList.get(nodeList.size() - 1);
	}
}
