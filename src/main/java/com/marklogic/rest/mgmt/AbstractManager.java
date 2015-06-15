package com.marklogic.rest.mgmt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.clientutil.LoggingObject;

public class AbstractManager extends LoggingObject {

    protected ObjectMapper objectMapper = new ObjectMapper();

    protected JsonNode parseJson(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException(format("Unable to parse JSON: %s", e.getMessage()), e);
        }
    }
}
