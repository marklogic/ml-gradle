package com.marklogic.rest.mgmt.security;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
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

    @Override
    protected String[] getDeleteParams(JsonNode node) {
        return getUpdateParams(node);
    }

    @Override
    protected String[] getUpdateParams(JsonNode node) {
        List<String> params = new ArrayList<String>();
        params.add("document-uri");
        params.add(node.get("document-uri").asText());
        if (node.has("namespace")) {
            params.add("namespace");
            params.add(node.get("namespace").asText());
        }
        if (node.has("modules-database")) {
            params.add("modules-database");
            params.add(node.get("modules-database").asText());
        }
        return params.toArray(new String[] {});
    }
}
