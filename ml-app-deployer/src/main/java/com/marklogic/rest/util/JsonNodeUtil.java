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
package com.marklogic.rest.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.command.ResourceReference;
import com.marklogic.mgmt.util.ObjectMapperFactory;

import java.io.File;
import java.util.*;
import java.util.function.BiPredicate;

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
	 * Returns a new list containing the results of merging object nodes in the given list where the given
	 * BiPredicate returns true for those object nodes equaling each other.
	 *
	 * @param list
	 * @param objectNodeEqualityTester
	 * @return
	 */
	public static List<ResourceReference> mergeObjectNodeList(List<ResourceReference> list,
	                                                   BiPredicate<ResourceReference, ResourceReference> objectNodeEqualityTester) {
		List<ResourceReference> listOfMergedReferences = new ArrayList<>();
		for (ResourceReference resourceReference : list) {
			if (listOfMergedReferences.isEmpty()) {
				listOfMergedReferences.add(resourceReference);
				continue;
			}

			int indexOfMatch = -1;
			for (int i = 0; i < listOfMergedReferences.size(); i++) {
				if (objectNodeEqualityTester.test(resourceReference, listOfMergedReferences.get(i))) {
					indexOfMatch = i;
					break;
				}
			}

			if (indexOfMatch > -1) {
				// Merge the current resource into the existing one so the current one overwrites common
				// single-value properties
				ResourceReference matchingReference = listOfMergedReferences.get(indexOfMatch);
				ObjectNode merged = JsonNodeUtil.mergeObjectNodes(matchingReference.getObjectNode(), resourceReference.getObjectNode());
				matchingReference.setObjectNode(merged);
				matchingReference.getFiles().addAll(resourceReference.getFiles());
			} else {
				listOfMergedReferences.add(resourceReference);
			}
		}

		return listOfMergedReferences;
	}

	/**
	 * Merges each node into the next node in the sequence.
	 *
	 * @param nodes
	 * @return
	 */
	public static ObjectNode mergeObjectNodes(ObjectNode... nodes) {
		List<ObjectNode> nodeList = new ArrayList<>();
		Set<String> fieldNames = new LinkedHashSet<>();
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
					// If the source field doesn't exist, then the target field wins by default
					continue;
				} else if (targetField == null) {
					// If the target field doesn't exist, the source field wins
					target.set(name, sourceField);
				} else if (sourceField.isArray() && targetField.isArray()) {
					/**
					 * For an array, values from the target array are added to the source array, and then the source
					 * array is set on the target.
					 */
					ArrayNode sourceArray = (ArrayNode) sourceField;
					ArrayNode targetArray = (ArrayNode) targetField;

					// Need to make a new array so that the sourceArray isn't modified
					ArrayNode newArray = ObjectMapperFactory.getObjectMapper().createArrayNode();
					newArray.addAll(sourceArray);

					targetArray.forEach(node -> {
						if (!arrayContains(newArray, node)) {
							newArray.add(node);
						}
					});
					target.set(name, newArray);
				}

				/**
				 * If none of the conditionals above are true, then the source and target fields both exist, and they're
				 * not arrays, in which case we do nothing, which means the target field wins.
				 */
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
