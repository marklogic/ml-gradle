package com.marklogic.mgmt;

import com.marklogic.client.ext.helper.LoggingObject;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ClassUtils;

public class AbstractManager extends LoggingObject {

    protected PayloadParser payloadParser = new PayloadParser();

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

    protected String getResourceId(String payload) {
        return payloadParser.getPayloadFieldValue(payload, getIdFieldName());
    }

    protected ResponseEntity<String> putPayload(ManageClient client, String path, String payload) {
        if (payloadParser.isJsonPayload(payload)) {
            return client.putJson(path, payload);
        }
        return client.putXml(path, payload);
    }

    protected ResponseEntity<String> postPayload(ManageClient client, String path, String payload) {
        if (payloadParser.isJsonPayload(payload)) {
            return client.postJson(path, payload);
        }
        return client.postXml(path, payload);
    }
}
