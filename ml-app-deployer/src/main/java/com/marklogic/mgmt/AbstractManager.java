/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
	 * Some payloads - such as a server payload that uses external security - require a condition to determine if the
	 * security user should be used or not.
	 *
	 * @param payload
	 * @return
	 */
	protected boolean useSecurityUser(String payload) {
    	return useSecurityUser();
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
        boolean requiresSecurityUser = useSecurityUser(payload);
        try {
	        if (payloadParser.isJsonPayload(payload)) {
		        return requiresSecurityUser ? client.putJsonAsSecurityUser(path, payload) : client.putJson(path, payload);
	        }
	        return requiresSecurityUser ? client.putXmlAsSecurityUser(path, payload) : client.putXml(path, payload);
        } catch (RuntimeException ex) {
	        logger.error(format("Error occurred while sending PUT request to %s; logging request body to assist with debugging: %s", path, payload));
	        throw ex;
        }
    }

    protected ResponseEntity<String> postPayload(ManageClient client, String path, String payload) {
        boolean requiresSecurityUser = useSecurityUser(payload);
        try {
	        if (payloadParser.isJsonPayload(payload)) {
		        return requiresSecurityUser ? client.postJsonAsSecurityUser(path, payload) : client.postJson(path, payload);
	        }
	        return requiresSecurityUser ? client.postXmlAsSecurityUser(path, payload) : client.postXml(path, payload);
        } catch (RuntimeException ex) {
        	logger.error(format("Error occurred while sending POST request to %s; logging request body to assist with debugging: %s", path, payload));
        	throw ex;
        }
    }
}
