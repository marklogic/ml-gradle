package com.rjrudin.marklogic.mgmt.security;

import com.rjrudin.marklogic.mgmt.AbstractResourceManager;
import com.rjrudin.marklogic.mgmt.ManageClient;

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
