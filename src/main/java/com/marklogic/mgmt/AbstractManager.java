package com.marklogic.mgmt;

import com.marklogic.client.ext.helper.LoggingObject;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ClassUtils;

public class AbstractManager extends LoggingObject {

    protected PayloadParser payloadParser = new PayloadParser();

    /**
     * Manager classes that need to connect to ML as a user with the manage-admin and security roles (e.g. all the
     * classes for Security resources) should override this to return true.
     *
     * The main use case for this is while an application may define a user with the manage-admin role that can be used
     * for deploying most resources, that user must first be created. And thus, some user with at least the manage-admin
     * and security roles must already exist and must be used to create that user.
     *
     * @return
     */
    protected boolean useSecurityUser() {
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

    protected String getResourceId(String payload) {
        return payloadParser.getPayloadFieldValue(payload, getIdFieldName());
    }

    protected ResponseEntity<String> putPayload(ManageClient client, String path, String payload) {
        boolean requiresSecurityUser = useSecurityUser();
        if (payloadParser.isJsonPayload(payload)) {
            return requiresSecurityUser ? client.putJsonAsSecurityUser(path, payload) : client.putJson(path, payload);
        }
        return requiresSecurityUser ? client.putXmlAsSecurityUser(path, payload) : client.putXml(path, payload);
    }

    protected ResponseEntity<String> postPayload(ManageClient client, String path, String payload) {
        boolean requiresSecurityUser = useSecurityUser();
        if (payloadParser.isJsonPayload(payload)) {
            return requiresSecurityUser ? client.postJsonAsSecurityUser(path, payload) : client.postJson(path, payload);
        }
        return requiresSecurityUser ? client.postXmlAsSecurityUser(path, payload) : client.postXml(path, payload);
    }
}
