package com.rjrudin.marklogic.mgmt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rjrudin.marklogic.rest.util.Fragment;

/**
 * Utility class for parsing a JSON or XML payload and extracting values.
 */
public class PayloadParser {

    private ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode parseJson(String json) {
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
        if (isJsonPayload(payload)) {
            JsonNode node = parseJson(payload);
            if (!node.has(fieldName)) {
                throw new RuntimeException("Cannot get field value from JSON; field name: " + fieldName + "; JSON: "
                        + payload);
            }
            return node.get(fieldName).asText();
        } else {
            Fragment f = new Fragment(payload);
            String xpath = String.format("/node()/*[local-name(.) = '%s']", fieldName);
            if (!f.elementExists(xpath)) {
                throw new RuntimeException("Cannot get field value from XML at path: " + xpath + "; XML: " + payload);
            }
            return f.getElementValues(xpath).get(0);
        }
    }

    public boolean isJsonPayload(String payload) {
        return payload.trim().startsWith("{");
    }
}
