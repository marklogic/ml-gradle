package com.marklogic.mgmt.resource.security;

import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.mgmt.ManageClient;

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
