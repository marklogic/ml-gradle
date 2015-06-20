package com.marklogic.rest.mgmt.security;

import com.marklogic.rest.mgmt.AbstractResourceManager;
import com.marklogic.rest.mgmt.ManageClient;

public class CertificateAuthorityManager extends AbstractResourceManager {

    public CertificateAuthorityManager(ManageClient client) {
        super(client);
    }

    @Override
    public String getResourcesPath() {
        return "/manage/v2/certificate-authorities";
    }

    @Override
    protected String getIdFieldName() {
        return "certificate-id";
    }

}
