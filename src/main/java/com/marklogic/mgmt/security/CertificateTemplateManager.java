package com.marklogic.mgmt.security;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.mgmt.AbstractResourceManager;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.rest.util.Fragment;

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
    protected boolean useAdminUser() {
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
        ObjectNode node = new ObjectMapper().createObjectNode();
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

    public Fragment getCertificatesForTemplate(String templateIdOrName) {
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("operation", "get-certificates-for-template");

        String json = node.toString();
        String xml = postPayload(getManageClient(), getResourcePath(templateIdOrName), json).getBody();
        return new Fragment(xml);
    }
}
