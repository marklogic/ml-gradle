package com.marklogic.rest.mgmt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.clientutil.LoggingObject;

public class AbstractManager extends LoggingObject {

    protected ObjectMapper objectMapper = new ObjectMapper();

    protected String format(String s, Object... args) {
        return String.format(s, args);
    }

    protected JsonNode parseJson(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse JSON: " + e.getMessage(), e);
        }
    }
}
