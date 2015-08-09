package com.rjrudin.marklogic.mgmt.security;

import com.rjrudin.marklogic.mgmt.AbstractResourceManager;
import com.rjrudin.marklogic.mgmt.ManageClient;

public class ExternalSecurityManager extends AbstractResourceManager {

    public ExternalSecurityManager(ManageClient client) {
        super(client);
    }

    @Override
    protected boolean useAdminUser() {
        return true;
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
