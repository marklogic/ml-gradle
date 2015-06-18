package com.marklogic.rest.mgmt.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.rest.mgmt.AbstractResourceManager;
import com.marklogic.rest.mgmt.ManageClient;

public class PrivilegeManager extends AbstractResourceManager {

    public PrivilegeManager(ManageClient client) {
        super(client);
    }

    @Override
    protected String[] getResourceParams(JsonNode node) {
        return new String[] { "kind", node.get("kind").asText() };
    }
}
