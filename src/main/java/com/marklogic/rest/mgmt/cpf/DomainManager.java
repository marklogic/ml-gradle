package com.marklogic.rest.mgmt.cpf;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.rest.mgmt.AbstractManager;
import com.marklogic.rest.mgmt.ManageClient;
import com.marklogic.rest.util.Fragment;

public class DomainManager extends AbstractManager {

    private ManageClient client;

    public DomainManager(ManageClient client) {
        this.client = client;
    }

    public boolean exists(String databaseIdOrName, String domainIdOrName) {
        Fragment f = client.getXml(format("/manage/v2/databases/%s/domains", databaseIdOrName));
        String xpath = "/node()/*[local-name(.) = 'list-items']/*[local-name(.) = 'list-item']"
                + "[*[local-name(.) = 'nameref'] = '%s' or *[local-name(.) = 'idref'] = '%s']";
        xpath = format(xpath, domainIdOrName, domainIdOrName);
        return f.elementExists(xpath);
    }

    public void save(String databaseIdOrName, String json) {
        JsonNode node = parseJson(json);
        String idFieldName = "domain-name";
        if (!node.has(idFieldName)) {
            throw new RuntimeException("Cannot save resource, JSON does not contains ID field name of: " + idFieldName
                    + "; JSON: " + json);
        }
        String name = node.get(idFieldName).asText();
        String label = "domain";
        if (exists(databaseIdOrName, name)) {
            String path = format("/manage/v2/databases/%s/domains/%s/properties", databaseIdOrName, name);

            logger.info(format("Found %s with name of %s, so updating ", label, path));
            client.putJson(path, json);
            logger.info(format("Updated %s at %s", label, path));
        } else {
            logger.info(format("Creating %s: %s", label, name));
            client.postJson(format("/manage/v2/databases/%s/domains", databaseIdOrName), json);
            logger.info(format("Created %s: %s", label, name));
        }
    }

}
