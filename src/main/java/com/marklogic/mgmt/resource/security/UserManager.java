package com.marklogic.mgmt.resource.security;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.AbstractResourceManager;

public class UserManager extends AbstractResourceManager {

    public UserManager(ManageClient client) {
        super(client);
    }

	@Override
	protected boolean useSecurityUser() {
    	return true;
	}

}
