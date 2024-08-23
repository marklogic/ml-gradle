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
package com.marklogic.mgmt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import com.marklogic.rest.util.Fragment;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class for parsing a JSON or XML payload and extracting values.
 */
public class PayloadParser {

    private ObjectMapper objectMapper;

    public JsonNode parseJson(String json) {
    	if (objectMapper == null) {
    		objectMapper = ObjectMapperFactory.getObjectMapper();
	    }
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to parse JSON: %s", e.getMessage()), e);
        }
    }

    public String getPayloadName(String payload, String idFieldName) {
        return getPayloadFieldValue(payload, idFieldName);
    }

	public String getPayloadFieldValue(String payload, String fieldName) {
    	return getPayloadFieldValue(payload, fieldName, true);
	}

	public String getPayloadFieldValue(String payload, String fieldName, boolean throwErrorIfNotFound) {
        if (isJsonPayload(payload)) {
            JsonNode node = parseJson(payload);
            if (!node.has(fieldName)) {
            	if (throwErrorIfNotFound) {
		            throw new RuntimeException("Cannot get field value from JSON; field name: " + fieldName + "; JSON: "
			            + payload);
	            } else {
            		return null;
	            }
            }
            return node.get(fieldName).isTextual() ? node.get(fieldName).asText() : node.get(fieldName).toString();
        } else {
            Fragment f = new Fragment(payload);
            String xpath = String.format("/node()/*[local-name(.) = '%s']", fieldName);
            if (!f.elementExists(xpath)) {
            	if (throwErrorIfNotFound) {
		            throw new RuntimeException("Cannot get field value from XML at path: " + xpath + "; XML: " + payload);
	            } else {
            		return null;
	            }
            }
            return f.getElementValues(xpath).get(0);
        }
    }

    public boolean isJsonPayload(String payload) {
    	if (payload == null) {
    		return false;
	    }
        String s = payload.trim();
        return s.startsWith("{") || s.startsWith("[");
    }

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * Returns a payload consisting of only properties with the given property names. Only supports including immediate
	 * child properties of the payload.
	 *
	 * @param payload
	 * @param propertyNames
	 * @return
	 */
	public String includeProperties(String payload, String... propertyNames) {
		List<String> propertyNameList = Arrays.asList(propertyNames);

		if (isJsonPayload(payload)) {
			JsonNode json = parseJson(payload);
			ObjectNode node = (ObjectNode) json;
			Iterator<String> it = json.fieldNames();
			while (it.hasNext()) {
				String name = it.next();
				if (!propertyNameList.contains(name)) {
					it.remove();
				}
			}
			payload = node.toString();
		} else {
			//assume XML
			Fragment frag = new Fragment(payload);
			Element doc = frag.getInternalDoc().getRootElement();
			for (Element child : doc.getChildren()) {
				if (!propertyNameList.contains(child.getName())) {
					child.detach();
				}
			}
			payload = new XMLOutputter().outputString(doc);
		}
		return payload;
	}

	/**
	 * Returns a payload consisting of the original properties in the payload minus any properties matching the given
	 * property names. Only supports excluding immediate child properties in the payload.
	 *
	 * @param payload
	 * @param propertyNames
	 * @return
	 */
	public String excludeProperties(String payload, String... propertyNames) {
		if (isJsonPayload(payload)) {
			JsonNode json = parseJson(payload);
			for (String propertyName : propertyNames) {
				if (json.has(propertyName)) {
					ObjectNode node = (ObjectNode) json;
					node.remove(propertyName);
				}
			}
			payload = json.toString();
		} else {
			//assume XML
			Fragment frag = new Fragment(payload);
			Element doc = frag.getInternalDoc().getRootElement();
			// For XML, the propertyName needs to be an XPath expression
			for (String propertyName : propertyNames) {
				List<Element> elements = frag.getElements(propertyName);
				if (elements != null) {
					for (Element el : elements) {
						el.detach();
					}
				}
			}
			payload = new XMLOutputter().outputString(doc);
		}
		return payload;
	}
}
