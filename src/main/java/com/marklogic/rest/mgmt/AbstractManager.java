package com.marklogic.rest.mgmt;

import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.clientutil.LoggingObject;

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
}
