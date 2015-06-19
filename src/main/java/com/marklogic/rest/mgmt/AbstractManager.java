package com.marklogic.rest.mgmt;

import org.springframework.http.ResponseEntity;
import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.clientutil.LoggingObject;
import com.marklogic.rest.util.Fragment;

public class AbstractManager extends LoggingObject {

    protected ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Assumes the resource name is based on the class name - e.g. RoleManager would have a resource name of "role".
     * 
     * @return
     */
    protected String getResourceName() {
        String name = ClassUtils.getShortName(getClass());
        name = name.replace("Manager", "");
        return name.toLowerCase();
    }

    /**
     * Assumes the field name of the resource ID - which is used to determine existence - is the resource name plus
     * "-name". So RoleManager would have an ID field name of "role-name".
     * 
     * @return
     */
    protected String getIdFieldName() {
        return getResourceName() + "-name";
    }

    protected JsonNode parseJson(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException(format("Unable to parse JSON: %s", e.getMessage()), e);
        }
    }

    protected String getPayloadName(String payload) {
        String idFieldName = getIdFieldName();
        if (isJsonPayload(payload)) {
            JsonNode node = parseJson(payload);
            if (!node.has(idFieldName)) {
                throw new RuntimeException("Cannot get resource name from JSON; expected ID field name: " + idFieldName
                        + "; JSON: " + payload);
            }
            return node.get(idFieldName).asText();
        } else {
            Fragment f = new Fragment(payload);
            String xpath = format("/node()/*[local-name(.) = '%s']", idFieldName);
            if (!f.elementExists(xpath)) {
                throw new RuntimeException("Cannot get resource name from XML at path: " + xpath + "; XML: " + payload);
            }
            return f.getElementValues(xpath).get(0);
        }
    }

    protected boolean isJsonPayload(String payload) {
        return payload.trim().startsWith("{");
    }

    protected ResponseEntity<String> putPayload(ManageClient client, String path, String payload) {
        if (isJsonPayload(payload)) {
            return client.putJson(path, payload);
        }
        return client.putXml(path, payload);
    }

    protected ResponseEntity<String> postPayload(ManageClient client, String path, String payload) {
        if (isJsonPayload(payload)) {
            return client.postJson(path, payload);
        }
        return client.postXml(path, payload);
    }
}
