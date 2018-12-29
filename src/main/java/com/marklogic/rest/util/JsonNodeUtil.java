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

	/**
	 * Merges each node into the next node in the sequence.
	 *
	 * @param nodes
	 * @return
	 */
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

					sourceArray.forEach(node -> {
						if (!arrayContains(targetArray, node)) {
							targetArray.add(node);
						}
					});
				}
			}
		}

		return nodeList.get(nodeList.size() - 1);
	}

	/**
	 * Prevents the addition of duplicate nodes into an array while merging two arrays together.
	 *
	 * @param array
	 * @param jsonNode
	 * @return
	 */
	private static boolean arrayContains(ArrayNode array, JsonNode jsonNode) {
		Iterator<JsonNode> iter = array.elements();
		while (iter.hasNext()) {
			JsonNode node = iter.next();
			if (node.equals(jsonNode)) {
				return true;
			}
		}
		return false;
	}
}
