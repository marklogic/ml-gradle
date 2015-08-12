package com.rjrudin.marklogic.mgmt.cpf;

import com.rjrudin.marklogic.mgmt.AbstractManager;
import com.rjrudin.marklogic.mgmt.ManageClient;
import com.rjrudin.marklogic.rest.util.ResourcesFragment;

/**
 * Can't extend AbstractResourceManager because the URLs for CPF resources require a database ID.
 */
public abstract class AbstractCpfResourceManager extends AbstractManager {

    private ManageClient manageClient;

    public AbstractCpfResourceManager(ManageClient client) {
        this.manageClient = client;
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
        return new ResourcesFragment(manageClient.getXml(getResourcesPath(databaseIdOrName)));
    }

    public boolean exists(String databaseIdOrName, String resourceIdOrName) {
        return getAsXml(databaseIdOrName).resourceExists(resourceIdOrName);
    }

    public void save(String databaseIdOrName, String payload) {
        String name = getResourceId(payload);
        String label = getResourceName();
        if (exists(databaseIdOrName, name)) {
            String path = getPropertiesPath(databaseIdOrName, name);
            logger.info(format("Found %s with name of %s, so updating ", label, path));
            putPayload(manageClient, path, payload);
            logger.info(format("Updated %s at %s", label, path));
        } else {
            logger.info(format("Creating %s: %s", label, name));
            postPayload(manageClient, getResourcesPath(databaseIdOrName), payload);
            logger.info(format("Created %s: %s", label, name));
        }
    }

    public ManageClient getManageClient() {
        return manageClient;
    }
}
