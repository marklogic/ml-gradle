package com.rjrudin.marklogic.mgmt;

import org.springframework.http.ResponseEntity;
import org.springframework.util.ClassUtils;

import com.rjrudin.marklogic.client.LoggingObject;

public class AbstractManager extends LoggingObject {

    protected PayloadParser payloadParser = new PayloadParser();

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

    protected String getPayloadName(String payload) {
        return payloadParser.getPayloadFieldValue(payload, getIdFieldName());
    }

    protected ResponseEntity<String> putPayload(ManageClient client, String path, String payload) {
        boolean useAdmin = useAdminUser();
        if (payloadParser.isJsonPayload(payload)) {
            return useAdmin ? client.putJsonAsAdmin(path, payload) : client.putJson(path, payload);
        }
        return useAdmin ? client.putXmlAsAdmin(path, payload) : client.putXml(path, payload);
    }

    protected ResponseEntity<String> postPayload(ManageClient client, String path, String payload) {
        boolean useAdmin = useAdminUser();
        if (payloadParser.isJsonPayload(payload)) {
            return useAdmin ? client.postJsonAsAdmin(path, payload) : client.postJson(path, payload);
        }
        return useAdmin ? client.postXmlAsAdmin(path, payload) : client.postXml(path, payload);
    }
}
