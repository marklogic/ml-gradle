package com.marklogic.rest.mgmt.security;

import com.marklogic.rest.mgmt.AbstractResourceManager;
import com.marklogic.rest.mgmt.ManageClient;

public class PrivilegeManager extends AbstractResourceManager {

    public PrivilegeManager(ManageClient client) {
        super(client);
    }

    @Override
    protected String[] getResourceParams(String payload) {
        return new String[] { "kind", getPayloadFieldValue(payload, "kind") };
    }
}
