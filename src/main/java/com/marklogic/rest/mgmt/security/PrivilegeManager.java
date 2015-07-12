package com.marklogic.rest.mgmt.security;

import com.marklogic.rest.mgmt.AbstractResourceManager;
import com.marklogic.rest.mgmt.ManageClient;

public class PrivilegeManager extends AbstractResourceManager {

    public PrivilegeManager(ManageClient client) {
        super(client);
    }

    @Override
    protected boolean useAdminUser() {
        return true;
    }

    @Override
    protected String[] getUpdateResourceParams(String payload) {
        return new String[] { "kind", payloadParser.getPayloadFieldValue(payload, "kind") };
    }
}
