package com.marklogic.rest.mgmt.security;

import com.marklogic.rest.mgmt.AbstractResourceManager;
import com.marklogic.rest.mgmt.ManageClient;

public class ExternalSecurityManager extends AbstractResourceManager {

    public ExternalSecurityManager(ManageClient client) {
        super(client);
    }

    @Override
    public String getResourcesPath() {
        return "/manage/v2/external-security";
    }

    @Override
    protected String getIdFieldName() {
        return "external-security-name";
    }

}
