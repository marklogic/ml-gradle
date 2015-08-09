package com.rjrudin.marklogic.mgmt.security;

import com.rjrudin.marklogic.mgmt.AbstractResourceManager;
import com.rjrudin.marklogic.mgmt.ManageClient;

public class UserManager extends AbstractResourceManager {

    public UserManager(ManageClient client) {
        super(client);
    }

    @Override
    protected boolean useAdminUser() {
        return true;
    }

}
