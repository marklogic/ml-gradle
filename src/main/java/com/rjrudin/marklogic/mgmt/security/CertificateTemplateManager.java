package com.rjrudin.marklogic.mgmt.security;

import com.rjrudin.marklogic.mgmt.AbstractResourceManager;
import com.rjrudin.marklogic.mgmt.ManageClient;

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
    public String getResourcePath(String resourceNameOrId) {
        String id = getIdForName(resourceNameOrId);
        return format("%s/%s", getResourcesPath(), id);
    }

    @Override
    public String getPropertiesPath(String resourceNameOrId) {
        return format("%s/properties", getResourcePath(resourceNameOrId));
    }

    public String getIdForName(String name) {
        return getAsXml().getIdForNameOrId(name);
    }
}
