package com.marklogic.rest.mgmt;

import com.fasterxml.jackson.databind.JsonNode;
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

    /**
     * The root element differs in the XML return by each "/manage/v2/(resource name)" endpoint. This defaults to the
     * resource name plus "-default-list". So RoleManager would have a root element name of "role-default-list".
     * 
     * @return
     */
    protected String getResourcesRootElementName() {
        return getResourceName() + "-default-list";
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

    public void save(String json) {
        JsonNode node = parseJson(json);
        String idFieldName = getIdFieldName();
        if (!node.has(idFieldName)) {
            throw new RuntimeException("Cannot save resource, JSON does not contains ID field name of: " + idFieldName
                    + "; JSON: " + json);
        }
        String name = node.get(getIdFieldName()).asText();
        String label = getResourceName();
        if (exists(name)) {
            String path = getPropertiesPath(name);
            path = appendParamsAndValuesToPath(path, getResourceParams(node));

            logger.info(format("Found %s with name of %s, so updating ", label, path));
            manageClient.putJson(path, json);
            logger.info(format("Updated %s at %s", label, path));
        } else {
            logger.info(format("Creating %s: %s", label, name));
            manageClient.postJson(getResourcesPath(), json);
            logger.info(format("Created %s: %s", label, name));
        }
    }

    public void delete(String json) {
        JsonNode node = parseJson(json);
        String name = node.get(getIdFieldName()).asText();

        String label = getResourceName();
        if (!exists(name)) {
            logger.info(format("Could not find %s with name or ID of %s, so not deleting", label, name));
        } else {
            String path = getResourcePath(name);
            path = appendParamsAndValuesToPath(path, getResourceParams(node));

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
    protected String[] getResourceParams(JsonNode node) {
        return new String[] {};
    }

    protected ManageClient getManageClient() {
        return manageClient;
    }

}
