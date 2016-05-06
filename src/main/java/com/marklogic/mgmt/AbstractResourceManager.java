package com.marklogic.mgmt;

import org.springframework.http.ResponseEntity;

import com.marklogic.rest.util.Fragment;
import com.marklogic.rest.util.ResourcesFragment;

/**
 * This class makes a number of assumptions in order to simplify the implementation of common operations for a MarkLogic
 * management resource. Feel free to override the methods in here in a subclass when those assumptions don't work for a
 * particular resource.
 */
public abstract class AbstractResourceManager extends AbstractManager implements ResourceManager {

    private ManageClient manageClient;
    private boolean updateAllowed = true;

    public AbstractResourceManager(ManageClient client) {
        this.manageClient = client;
    }

    public String getResourcesPath() {
        return format("/manage/v2/%ss", getResourceName());
    }

    public String getResourcePath(String resourceNameOrId, String... resourceUrlParams) {
        resourceNameOrId = encodeResourceId(resourceNameOrId);
        return appendParamsAndValuesToPath(format("%s/%s", getResourcesPath(), resourceNameOrId), resourceUrlParams);
    }

    public String getPropertiesPath(String resourceNameOrId, String... resourceUrlParams) {
        return appendParamsAndValuesToPath(format("%s/properties", getResourcePath(resourceNameOrId)),
                resourceUrlParams);
    }

    /**
     * TODO Not sure yet whether we need to account for resourceUrlParams when doing an existence check.
     */
    public boolean exists(String resourceNameOrId, String... resourceUrlParams) {
        return getAsXml().resourceExists(resourceNameOrId);
    }

    public ResourcesFragment getAsXml() {
        Fragment f = useAdminUser() ? manageClient.getXmlAsAdmin(getResourcesPath())
                : manageClient.getXml(getResourcesPath());
        return new ResourcesFragment(f);
    }

    public Fragment getAsXml(String resourceNameOrId, String... resourceUrlParams) {
        String path = appendParamsAndValuesToPath(getResourcePath(resourceNameOrId, resourceUrlParams));
        return useAdminUser() ? manageClient.getXmlAsAdmin(path) : manageClient.getXml(path);
    }

    public Fragment getPropertiesAsXml(String resourceNameOrId, String... resourceUrlParams) {
        String path = appendParamsAndValuesToPath(getPropertiesPath(resourceNameOrId, resourceUrlParams));
        return useAdminUser() ? manageClient.getXmlAsAdmin(path) : manageClient.getXml(path);
    }

    public String getAsJson(String resourceNameOrId, String... resourceUrlParams) {
        String path = appendParamsAndValuesToPath(getPropertiesPath(resourceNameOrId, resourceUrlParams));
        return manageClient.getJson(path);
    }

    /**
     * Determines whether to create a new resource or update an existing one based on the contents of the payload.
     */
    public SaveReceipt save(String payload) {
        String resourceId = getResourceId(payload);
        String label = getResourceName();
        String path = null;
        ResponseEntity<String> response = null;
        if (exists(resourceId)) {
            if (updateAllowed) {
                return updateResource(payload, resourceId);
            } else {
                logger.info("Resource already exists and updates are not supported, so not updating: " + resourceId);
            }
        } else {
            logger.info(format("Creating %s: %s", label, resourceId));
            path = getCreateResourcePath(payload);
            response = postPayload(manageClient, path, payload);
            logger.info(format("Created %s: %s", label, resourceId));
        }
        return new SaveReceipt(resourceId, payload, path, response);
    }

    /**
     * Mimetypes are likely to have a "+" in them, which the Management REST API won't support in a path - it needs to
     * be encoded. Other resources could have a "+" in their ID value as well. However, doing a full encoding doesn't
     * seem to be a great idea, as that will e.g. encode a forward slash in a mimetype as well, which will result in a
     * 404.
     * 
     * @param idValue
     * @return
     */
    protected String encodeResourceId(String idValue) {
        return idValue != null ? idValue.replace("+", "%2B") : idValue;
    }

    /**
     * Most clients should just use the save method, but this is public for scenarios where a client knows an update
     * should be performed.
     * 
     * @param payload
     * @param resourceId
     * @return
     */
    public SaveReceipt updateResource(String payload, String resourceId) {
        String path = getPropertiesPath(resourceId);
        String label = getResourceName();
        path = appendParamsAndValuesToPath(path, getUpdateResourceParams(payload));
        logger.info(format("Found %s with name of %s, so updating at path %s", label, resourceId, path));
        ResponseEntity<String> response = putPayload(manageClient, path, payload);
        logger.info(format("Updated %s at %s", label, path));
        return new SaveReceipt(resourceId, payload, path, response);
    }

    protected String getCreateResourcePath(String payload) {
        return getResourcesPath();
    }

    @Override
    public DeleteReceipt deleteByIdField(String resourceIdFieldValue, String... resourceUrlParams) {
        String payload = "{\"%s\":\"%s\"}";
        return delete(format(payload, getIdFieldName(), resourceIdFieldValue), resourceUrlParams);
    }

    @Override
    public DeleteReceipt delete(String payload, String... resourceUrlParams) {
        String resourceId = getResourceId(payload);
        if (!exists(resourceId)) {
            logger.info(
                    format("Could not find %s with name or ID of %s, so not deleting", getResourceName(), resourceId));
            return new DeleteReceipt(resourceId, null, false);
        } else {
            String path = getResourcePath(resourceId, resourceUrlParams);
            path = appendParamsAndValuesToPath(path, getDeleteResourceParams(payload));
            deleteAtPath(path);
            return new DeleteReceipt(resourceId, path, true);
        }
    }

    /**
     * Convenience method for performing a delete once the correct path for the resource has been constructed.
     * 
     * @param path
     */
    public void deleteAtPath(String path) {
        String label = getResourceName();
        logger.info(format("Deleting %s at path %s", label, path));
        if (useAdminUser()) {
            manageClient.deleteAsAdmin(path);
        } else {
            manageClient.delete(path);
        }
        logger.info(format("Deleted %s at path %s", label, path));
    }

    /**
     * TODO Could use something nicer here, particularly to properly encode the parameter values.
     * 
     * @param path
     * @param paramsAndValues
     * @return
     */
    protected String appendParamsAndValuesToPath(String path, String... paramsAndValues) {
        if (paramsAndValues != null && paramsAndValues.length > 0) {
            if (path.contains("?")) {
                path += "&";
            } else {
                path += "?";
            }
            for (int i = 0; i < paramsAndValues.length; i += 2) {
                String name = paramsAndValues[i];
                String value = paramsAndValues[i + 1];
                if (name != null && value != null) {
                    if (i > 0) {
                        path += "&";
                    }
                    path += name + "=" + value;
                }
            }
        }
        return path;
    }

    /**
     * Can be overridden by subclass to provide custom querystring parameters.
     * 
     * @param payload
     *            XML or JSON payload
     * @return
     */
    protected String[] getUpdateResourceParams(String payload) {
        return new String[] {};
    }

    /**
     * Defaults to the "update" resource parameters.
     * 
     * @param payload
     *            XML or JSON payload
     * @return
     */
    protected String[] getDeleteResourceParams(String payload) {
        return getUpdateResourceParams(payload);
    }

    protected ManageClient getManageClient() {
        return manageClient;
    }

    public boolean isUpdateAllowed() {
        return updateAllowed;
    }

    public void setUpdateAllowed(boolean updateAllowed) {
        this.updateAllowed = updateAllowed;
    }

}
