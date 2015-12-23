package com.marklogic.mgmt.security;

import com.marklogic.mgmt.AbstractResourceManager;
import com.marklogic.mgmt.ManageClient;

public class UserManager extends AbstractResourceManager {

    public UserManager(ManageClient client) {
        super(client);
    }

    @Override
    protected boolean useAdminUser() {
        return true;
    }

}
