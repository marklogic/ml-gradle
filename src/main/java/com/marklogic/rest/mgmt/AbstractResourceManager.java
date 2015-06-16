package com.marklogic.rest.mgmt;

import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.rest.util.Fragment;

/**
 * This class makes a number of assumptions in order to simplify the implementation of common operations for a MarkLogic
 * management resource. Feel free to override the methods in here in a subclass when those assumptions don't work for a
 * particular resource.
 */
public abstract class AbstractResourceManager extends AbstractManager implements ResourceManager {

    private ManageClient client;

    public AbstractResourceManager(ManageClient client) {
        this.client = client;
    }

    /**
     * Assumes the resource name is based on the class name - e.g. RoleManager would have a resource name of "role".
     * 
     * @return
     */
    protected String getResourceName() {
        String name = ClassUtils.getShortName(getClass());
        name = name.replace("Manager", "");
        return name.toLowerCase();
    }

    /**
     * Assumes the field name of the resource ID - which is used to determine existence - is the resource name plus
     * "-name". So RoleManager would have an ID field name of "role-name".
     * 
     * @return
     */
    protected String getIdFieldName() {
        return getResourceName() + "-name";
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
        String xpath = format("/msec:%s/msec:list-items/msec:list-item[msec:nameref = '%s' or msec:idref = '%s']",
                getResourcesRootElementName(), resourceNameOrId, resourceNameOrId);
        Fragment f = client.getXml(getResourcesPath());
        return f.elementExists(xpath);
    }

    public Fragment getAsXml(String resourceNameOrId) {
        return client.getXml(getResourcePath(resourceNameOrId));
    }

    public void save(String json) {
        JsonNode node = parseJson(json);
        String name = node.get(getIdFieldName()).asText();
        String label = getResourceName();
        if (exists(name)) {
            logger.info(format("Found %s with name of %s, so so updating", label, name));
            client.putJson(getPropertiesPath(name), json);
            logger.info(format("Updated %s: %s", label, name));
        } else {
            logger.info(format("Creating %s: %s", label, name));
            client.postJson(getResourcesPath(), json);
            logger.info(format("Created %s: %s", label, name));
        }
    }

    public void delete(String resourceNameOrId) {
        String label = getResourceName();
        if (!exists(resourceNameOrId)) {
            logger.info(format("Could not find %s with name or ID of %s, so not deleting", label, resourceNameOrId));
        } else {
            logger.info(format("Deleting %s: %s", label, resourceNameOrId));
            client.delete(getResourcePath(resourceNameOrId));
            logger.info(format("Deleted %s: %s", label, resourceNameOrId));
        }
    }

}
