package com.rjrudin.marklogic.mgmt;

import org.springframework.http.ResponseEntity;

import com.rjrudin.marklogic.rest.util.Fragment;
import com.rjrudin.marklogic.rest.util.ResourcesFragment;

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
        Fragment f = useAdminUser() ? manageClient.getXmlAsAdmin(getResourcesPath()) : manageClient
                .getXml(getResourcesPath());
        return new ResourcesFragment(f);
    }

    public Fragment getAsXml(String resourceNameOrId) {
        return useAdminUser() ? manageClient.getXmlAsAdmin(getResourcePath(resourceNameOrId)) : manageClient
                .getXml(getResourcePath(resourceNameOrId));
    }

    public Fragment getPropertiesAsXml(String resourceNameOrId) {
        return useAdminUser() ? manageClient.getXmlAsAdmin(getPropertiesPath(resourceNameOrId)) : manageClient
                .getXml(getPropertiesPath(resourceNameOrId));
    }

    public SaveReceipt save(String payload) {
        String resourceId = getResourceId(payload);
        String label = getResourceName();
        String path = null;
        ResponseEntity<String> response = null;
        if (exists(resourceId)) {
            path = getPropertiesPath(resourceId);
            path = appendParamsAndValuesToPath(path, getUpdateResourceParams(payload));
            logger.info(format("Found %s with name of %s, so updating at path %s", label, resourceId, path));
            response = putPayload(manageClient, path, payload);
            logger.info(format("Updated %s at %s", label, path));
        } else {
            logger.info(format("Creating %s: %s", label, resourceId));
            path = getCreateResourcePath(payload);
            response = postPayload(manageClient, path, payload);
            logger.info(format("Created %s: %s", label, resourceId));
        }
        return new SaveReceipt(resourceId, payload, path, response);
    }

    protected String getCreateResourcePath(String payload) {
        return getResourcesPath();
    }

    @Override
    public boolean deleteByIdField(String resourceIdFieldValue) {
        String payload = "{\"%s\":\"%s\"}";
        return delete(format(payload, getIdFieldName(), resourceIdFieldValue));
    }

    public boolean delete(String payload) {
        String resourceId = getResourceId(payload);
        String label = getResourceName();
        if (!exists(resourceId)) {
            logger.info(format("Could not find %s with name or ID of %s, so not deleting", label, resourceId));
            return false;
        } else {
            String path = getResourcePath(resourceId);
            path = appendParamsAndValuesToPath(path, getDeleteResourceParams(payload));

            logger.info(format("Deleting %s at path %s", label, path));
            if (useAdminUser()) {
                manageClient.deleteAsAdmin(path);
            } else {
                manageClient.delete(path);
            }
            logger.info(format("Deleted %s at path %s", label, path));
            return true;
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
