package com.marklogic.rest.mgmt.cpf;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.rest.mgmt.AbstractManager;
import com.marklogic.rest.mgmt.ManageClient;
import com.marklogic.rest.util.ResourcesFragment;

/**
 * Can't extend AbstractResourceManager because the URLs for CPF resources require a database ID.
 */
public abstract class AbstractCpfResourceManager extends AbstractManager {

    private ManageClient client;

    public AbstractCpfResourceManager(ManageClient client) {
        this.client = client;
    }

    public String getResourcesPath(String databaseIdOrName) {
        return format("/manage/v2/databases/%s/%ss", databaseIdOrName, getResourceName());
    }

    public String getResourcePath(String databaseIdOrName, String resourceNameOrId) {
        return format("%s/%s", getResourcesPath(databaseIdOrName), resourceNameOrId);
    }

    public String getPropertiesPath(String databaseIdOrName, String resourceNameOrId) {
        return format("%s/properties", getResourcePath(databaseIdOrName, resourceNameOrId));
    }

    public ResourcesFragment getAsXml(String databaseIdOrName) {
        return new ResourcesFragment(client.getXml(getResourcesPath(databaseIdOrName)));
    }

    public boolean exists(String databaseIdOrName, String resourceIdOrName) {
        return getAsXml(databaseIdOrName).resourceExists(resourceIdOrName);
    }

    public void save(String databaseIdOrName, String json) {
        JsonNode node = parseJson(json);
        String idFieldName = getIdFieldName();
        if (!node.has(idFieldName)) {
            throw new RuntimeException("Cannot save resource, JSON does not contains ID field name of: " + idFieldName
                    + "; JSON: " + json);
        }
        String name = node.get(idFieldName).asText();
        String label = getResourceName();
        if (exists(databaseIdOrName, name)) {
            String path = getPropertiesPath(databaseIdOrName, name);
            logger.info(format("Found %s with name of %s, so updating ", label, path));
            client.putJson(path, json);
            logger.info(format("Updated %s at %s", label, path));
        } else {
            logger.info(format("Creating %s: %s", label, name));
            client.postJson(getResourcesPath(databaseIdOrName), json);
            logger.info(format("Created %s: %s", label, name));
        }
    }
}
