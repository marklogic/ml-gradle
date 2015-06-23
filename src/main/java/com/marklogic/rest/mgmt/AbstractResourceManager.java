package com.marklogic.rest.mgmt;

import com.marklogic.rest.util.Fragment;
import com.marklogic.rest.util.ResourcesFragment;

/**
 * This class makes a number of assumptions in order to simplify the implementation of common operations for a MarkLogic
 * management resource. Feel free to override the methods in here in a subclass when those assumptions don't work for a
 * particular resource.
 */
public abstract class AbstractResourceManager extends AbstractManager implements ResourceManager {

    private ManageClient manageClient;

    public AbstractResourceManager(ManageClient client) {
        this.manageClient = client;
    }

    public String getResourcesPath() {
        return format("/manage/v2/%ss", getResourceName());
    }

    public String getResourcePath(String resourceNameOrId) {
        return format("%s/%s", getResourcesPath(), resourceNameOrId);
    }

    public String getPropertiesPath(String resourceNameOrId) {
        return format("%s/properties", getResourcePath(resourceNameOrId));
    }

    public boolean exists(String resourceNameOrId) {
        return getAsXml().resourceExists(resourceNameOrId);
    }

    public ResourcesFragment getAsXml() {
        return new ResourcesFragment(manageClient.getXml(getResourcesPath()));
    }

    public Fragment getAsXml(String resourceNameOrId) {
        return manageClient.getXml(getResourcePath(resourceNameOrId));
    }

    public Fragment getPropertiesAsXml(String resourceNameOrId) {
        return manageClient.getXml(getPropertiesPath(resourceNameOrId));
    }

    public void save(String payload) {
        String name = getPayloadName(payload);
        String label = getResourceName();
        if (exists(name)) {
            String path = getPropertiesPath(name);
            path = appendParamsAndValuesToPath(path, getUpdateResourceParams(payload));
            logger.info(format("Found %s with name of %s, so updating at path %s", label, name, path));
            putPayload(manageClient, path, payload);
            logger.info(format("Updated %s at %s", label, path));
        } else {
            logger.info(format("Creating %s: %s", label, name));
            postPayload(manageClient, getCreateResourcePath(payload), payload);
            logger.info(format("Created %s: %s", label, name));
        }
    }

    protected String getCreateResourcePath(String payload) {
        return getResourcesPath();
    }

    public void delete(String payload) {
        String name = getPayloadName(payload);
        String label = getResourceName();
        if (!exists(name)) {
            logger.info(format("Could not find %s with name or ID of %s, so not deleting", label, name));
        } else {
            String path = getResourcePath(name);
            path = appendParamsAndValuesToPath(path, getDeleteResourceParams(payload));

            logger.info(format("Deleting %s at path %s", label, path));
            manageClient.delete(path);
            logger.info(format("Deleted %s at path %s", label, path));
        }
    }

    protected String appendParamsAndValuesToPath(String path, String... paramsAndValues) {
        if (paramsAndValues.length > 0) {
            path += "?";
            for (int i = 0; i < paramsAndValues.length; i += 2) {
                if (i > 0) {
                    path += "&";
                }
                path += paramsAndValues[i] + "=" + paramsAndValues[i + 1];
            }
        }
        return path;
    }

    /**
     * Can be overridden by subclass to provide custom querystring parameters.
     * 
     * @param node
     * @return
     */
    protected String[] getUpdateResourceParams(String payload) {
        return new String[] {};
    }

    /**
     * Defaults to the "update" resource parameters.
     * 
     * @param payload
     * @return
     */
    protected String[] getDeleteResourceParams(String payload) {
        return getUpdateResourceParams(payload);
    }

    protected ManageClient getManageClient() {
        return manageClient;
    }

}
