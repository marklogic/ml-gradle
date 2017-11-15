package com.marklogic.mgmt;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import com.marklogic.rest.util.Fragment;

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
}
