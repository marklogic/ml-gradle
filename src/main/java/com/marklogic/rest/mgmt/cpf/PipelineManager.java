package com.marklogic.rest.mgmt.cpf;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.rest.mgmt.AbstractManager;
import com.marklogic.rest.mgmt.ManageClient;
import com.marklogic.rest.util.Fragment;

public class PipelineManager extends AbstractManager {

    private ManageClient client;

    public PipelineManager(ManageClient client) {
        this.client = client;
    }

    public boolean exists(String databaseIdOrName, String pipelineIdOrName) {
        Fragment f = client.getXml(format("/manage/v2/databases/%s/pipelines", databaseIdOrName));
        String xpath = "/node()/*[local-name(.) = 'list-items']/node()"
                + "[*[local-name(.) = 'nameref'] = '%s' or *[local-name(.) = 'idref'] = '%s']";
        xpath = format(xpath, pipelineIdOrName, pipelineIdOrName);
        return f.elementExists(xpath);
    }

    public void save(String databaseIdOrName, String json) {
        JsonNode node = parseJson(json);
        String idFieldName = "pipeline-name";
        if (!node.has(idFieldName)) {
            throw new RuntimeException("Cannot save resource, JSON does not contains ID field name of: " + idFieldName
                    + "; JSON: " + json);
        }
        String name = node.get(idFieldName).asText();
        String label = "domain";
        if (exists(databaseIdOrName, name)) {
            String path = format("/manage/v2/databases/%s/pipelines/%s/properties", databaseIdOrName, name);

            logger.info(format("Found %s with name of %s, so updating ", label, path));
            client.putJson(path, json);
            logger.info(format("Updated %s at %s", label, path));
        } else {
            logger.info(format("Creating %s: %s", label, name));
            client.postJson(format("/manage/v2/databases/%s/pipelines", databaseIdOrName), json);
            logger.info(format("Created %s: %s", label, name));
        }
    }

}
