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
     * Manager classes that need to connect to ML as a user with the admin role should override this to return true.
     * 
     * @return
     */
    protected boolean useAdminUser() {
        return false;
    }

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
        return getPayloadFieldValue(payload, getIdFieldName());
    }

    protected String getPayloadFieldValue(String payload, String fieldName) {
        if (isJsonPayload(payload)) {
            JsonNode node = parseJson(payload);
            if (!node.has(fieldName)) {
                throw new RuntimeException("Cannot get field value from JSON; field name: " + fieldName + "; JSON: "
                        + payload);
            }
            return node.get(fieldName).asText();
        } else {
            Fragment f = new Fragment(payload);
            String xpath = format("/node()/*[local-name(.) = '%s']", fieldName);
            if (!f.elementExists(xpath)) {
                throw new RuntimeException("Cannot get field value from XML at path: " + xpath + "; XML: " + payload);
            }
            return f.getElementValues(xpath).get(0);
        }
    }

    protected boolean isJsonPayload(String payload) {
        return payload.trim().startsWith("{");
    }

    protected ResponseEntity<String> putPayload(ManageClient client, String path, String payload) {
        boolean useAdmin = useAdminUser();
        if (isJsonPayload(payload)) {
            return useAdmin ? client.putJsonAsAdmin(path, payload) : client.putJson(path, payload);
        }
        return useAdmin ? client.putXmlAsAdmin(path, payload) : client.putXml(path, payload);
    }

    protected ResponseEntity<String> postPayload(ManageClient client, String path, String payload) {
        boolean useAdmin = useAdminUser();
        if (isJsonPayload(payload)) {
            return useAdmin ? client.postJsonAsAdmin(path, payload) : client.postJson(path, payload);
        }
        return useAdmin ? client.postXmlAsAdmin(path, payload) : client.postXml(path, payload);
    }
}
