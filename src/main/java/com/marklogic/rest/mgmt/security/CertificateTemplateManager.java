package com.marklogic.rest.mgmt.security;

import com.marklogic.rest.mgmt.AbstractResourceManager;
import com.marklogic.rest.mgmt.ManageClient;

public class CertificateTemplateManager extends AbstractResourceManager {

    public CertificateTemplateManager(ManageClient client) {
        super(client);
    }

    @Override
    public String getResourcesPath() {
        return "/manage/v2/certificate-templates";
    }

    @Override
    protected String getIdFieldName() {
        return "template-name";
    }

}
