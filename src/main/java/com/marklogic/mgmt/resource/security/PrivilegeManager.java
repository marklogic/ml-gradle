package com.marklogic.mgmt.resource.security;

import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.mgmt.ManageClient;

public class PrivilegeManager extends AbstractResourceManager {

    public PrivilegeManager(ManageClient client) {
        super(client);
    }

	@Override
	protected boolean useSecurityUser() {
		return true;
	}

    @Override
    protected String[] getUpdateResourceParams(String payload) {
        return new String[] { "kind", payloadParser.getPayloadFieldValue(payload, "kind") };
    }
}
