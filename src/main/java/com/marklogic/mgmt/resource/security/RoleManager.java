package com.marklogic.mgmt.resource.security;

import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.mgmt.ManageClient;

public class RoleManager extends AbstractResourceManager {

    public RoleManager(ManageClient client) {
        super(client);
    }

    @Override
    protected boolean useAdminUser() {
        return true;
    }

}
