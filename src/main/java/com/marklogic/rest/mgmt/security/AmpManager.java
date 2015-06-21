package com.marklogic.rest.mgmt.security;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.rest.mgmt.AbstractResourceManager;
import com.marklogic.rest.mgmt.ManageClient;
import com.marklogic.rest.util.Fragment;

public class AmpManager extends AbstractResourceManager {

    public AmpManager(ManageClient client) {
        super(client);
    }

    @Override
    protected String getIdFieldName() {
        return "local-name";
    }

    @Override
    protected String[] getUpdateResourceParams(String payload) {
        List<String> params = new ArrayList<String>();
        params.add("document-uri");
        if (isJsonPayload(payload)) {
            JsonNode node = parseJson(payload);
            params.add(node.get("document-uri").asText());
            if (node.has("namespace")) {
                params.add("namespace");
                params.add(node.get("namespace").asText());
            }
            if (node.has("modules-database")) {
                params.add("modules-database");
                params.add(node.get("modules-database").asText());
            }
        } else {
            Fragment f = new Fragment(payload);
            params.add(f.getElementValue("/node()/*[local-name(.) = 'document-uri']"));

            String val = f.getElementValue("/node()/*[local-name(.) = 'namespace']");
            if (val != null) {
                params.add("namespace");
                params.add(val);
            }

            val = f.getElementValue("/node()/*[local-name(.) = 'modules-database']");
            if (val != null) {
                params.add("modules-database");
                params.add(val);
            }
        }
        return params.toArray(new String[] {});
    }
}
