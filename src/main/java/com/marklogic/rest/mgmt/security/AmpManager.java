package com.marklogic.rest.mgmt.security;

import com.marklogic.rest.mgmt.AbstractResourceManager;
import com.marklogic.rest.mgmt.ManageClient;

public class AmpManager extends AbstractResourceManager {

    public AmpManager(ManageClient client) {
        super(client);
    }

    @Override
    protected String getIdFieldName() {
        return "local-name";
    }

}
