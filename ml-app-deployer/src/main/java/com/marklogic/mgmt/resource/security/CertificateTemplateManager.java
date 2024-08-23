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
package com.marklogic.mgmt.resource.security;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import com.marklogic.rest.util.Fragment;

import org.springframework.http.ResponseEntity;

/**
 * For 8.0-2, the docs suggest that either ID or name can be used for accessing a certificate template, but only ID
 * works. A JSON or XML file containing a template won't have an ID, as that's system-generated, so in order to build a
 * resource or properties path, we need to use the name to fetch the ID.
 */
public class CertificateTemplateManager extends AbstractResourceManager {

    public CertificateTemplateManager(ManageClient client) {
        super(client);
    }

	@Override
	protected boolean useSecurityUser() {
		return true;
	}

    @Override
    public String getResourcesPath() {
        return "/manage/v2/certificate-templates";
    }

    @Override
    protected String getIdFieldName() {
        return "template-name";
    }

    @Override
    public String getResourcePath(String resourceNameOrId, String... resourceUrlParams) {
        String id = getIdForName(resourceNameOrId);
        return format("%s/%s", getResourcesPath(), id);
    }

    @Override
    public String getPropertiesPath(String resourceNameOrId, String... resourceUrlParams) {
        return format("%s/properties", getResourcePath(resourceNameOrId));
    }

    public String getIdForName(String name) {
        return getAsXml().getIdForNameOrId(name);
    }

    public ResponseEntity<String> generateTemporaryCertificate(String templateIdOrName, String commonName) {
        return generateTemporaryCertificate(templateIdOrName, commonName, 365, null, null, true);
    }

    public ResponseEntity<String> generateTemporaryCertificate(String templateIdOrName, String commonName,
            int validFor, String dnsName, String ipAddress, boolean ifNecessary) {
        ObjectNode node = ObjectMapperFactory.getObjectMapper().createObjectNode();
        node.put("operation", "generate-temporary-certificate");
        node.put("valid-for", validFor);
        node.put("common-name", commonName);
        if (dnsName != null) {
            node.put("dns-name", dnsName);
        }
        if (ipAddress != null) {
            node.put("ip-addr", ipAddress);
        }
        node.put("if-necessary", ifNecessary);

        String json = node.toString();
        if (logger.isInfoEnabled()) {
            logger.info(format("Generating temporary certificate for template %s with payload: %s", templateIdOrName,
                    json));
        }
        return postPayload(getManageClient(), getResourcePath(templateIdOrName), json);
	 }

	/**
	 * Inserts a host certificate for a given template.
	 *
	 * Even though service allows multiple inserts in one call, this only inserts one host cert at a time.
	 *
	 * Note that the docs as of ML 9.0-5 are not correct for this operation - this method submits the correct JSON
	 * structure.
	 *
	 * @param templateIdOrName The template name to insert the host certificates
	 * @param pubCert the Public PEM formatted certificate
	 * @param privateKey the Private PEM formatted certificate
	 */
	public ResponseEntity<String> insertHostCertificate(String templateIdOrName, String pubCert, String privateKey) {
		ObjectNode command = ObjectMapperFactory.getObjectMapper().createObjectNode();
		command.put("operation", "insert-host-certificates");
		ArrayNode certs = ObjectMapperFactory.getObjectMapper().createArrayNode();

		ObjectNode certificate = ObjectMapperFactory.getObjectMapper().createObjectNode();
		ObjectNode cert = ObjectMapperFactory.getObjectMapper().createObjectNode();

		certificate.put("cert", pubCert);
		certificate.put("pkey", privateKey);
		cert.set("certificate", certificate);
		certs.add(cert);

		command.set("certificates", certs);

		String json = command.toString();
		if (logger.isInfoEnabled()) {
			// NOTE - should NOT print out private key - EVER
			logger.info(format("Inserting host certificate for template %s", templateIdOrName));
		}
		return postPayload(getManageClient(), getResourcePath(templateIdOrName), json);
	}

	/**
	 * Utility service to determine if certificates exist for a template.
	 *
	 * Used because ML9.0-5 (and prior) has bug for "needs-certificate" call
	 */
	public boolean certificateExists(String templateIdOrName) {
		return certificateExists(templateIdOrName, null);
	}

	/**
	 *
	 * @param templateIdOrName
	 * @param certificateHostName if not null, then true will be iff a certificate with the given templateIdOrName
	 *                            exists, and it has a host-name matching this parameter
	 * @return
	 */
	public boolean certificateExists(String templateIdOrName, String certificateHostName) {
		Fragment response = getCertificatesForTemplate(templateIdOrName);
		return certificateHostName != null ?
			response.elementExists(format("/msec:certificate-list/msec:certificate[msec:host-name = '%s']", certificateHostName)) :
			response.elementExists("/msec:certificate-list/msec:certificate");
	}

    public Fragment getCertificatesForTemplate(String templateIdOrName) {
        ObjectNode node = ObjectMapperFactory.getObjectMapper().createObjectNode();
        // Note the docs in ML 9.0-5 have a typo - it's "certificates", not "certificate"
        node.put("operation", "get-certificates-for-template");

        String json = node.toString();
        String xml = postPayload(getManageClient(), getResourcePath(templateIdOrName), json).getBody();
        return new Fragment(xml);
    }
}
