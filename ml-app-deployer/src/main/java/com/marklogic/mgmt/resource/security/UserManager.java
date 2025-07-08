/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
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
