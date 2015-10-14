package com.rjrudin.marklogic.mgmt.security;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.rjrudin.marklogic.mgmt.AbstractResourceManager;
import com.rjrudin.marklogic.mgmt.ManageClient;
import com.rjrudin.marklogic.rest.util.Fragment;

public class AmpManager extends AbstractResourceManager {

    public AmpManager(ManageClient client) {
        super(client);
        /*
         * Turning this off as part of version 2.0b10 - having issues with ML being able to create an amp but then
         * getting a 404 when it tries to update the amp. Seems to be due to using a modules database - viewing the
         * amp via the Mgmt API shows the modules database as being "filesystem", but the Admin app correctly shows
         * the modules database.
         */
        setUpdateAllowed(false);
    }

    @Override
    protected boolean useAdminUser() {
        return true;
    }

    @Override
    protected String getIdFieldName() {
        return "local-name";
    }

    @Override
    protected String[] getUpdateResourceParams(String payload) {
        List<String> params = new ArrayList<String>();
        params.add("document-uri");
        if (payloadParser.isJsonPayload(payload)) {
            JsonNode node = payloadParser.parseJson(payload);
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
