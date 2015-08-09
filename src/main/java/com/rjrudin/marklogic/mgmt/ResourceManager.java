package com.rjrudin.marklogic.mgmt;

import com.rjrudin.marklogic.rest.util.Fragment;
import com.rjrudin.marklogic.rest.util.ResourcesFragment;

/**
 * Interface that AbstractResourceCommand is bound to. If you can implement this - most likely by subclassing
 * AbstractResourceManager - then you should be able to subclass AbstractResourceCommand in order to very easily
 * implement a new command.
 */
public interface ResourceManager {

    /**
     * @param resourceNameOrId
     * @return true if a resource with the given name or ID exists, false otherwise
     */
    public boolean exists(String resourceNameOrId);

    /**
     * If a resource with a name in the the given payload exists, then update that resource; else create it.
     * 
     * @param payload
     */
    public void save(String payload);

    /**
     * Assumes that a resource ID field is in the payload, and then extracts that field value and 
     * tries to delete a resource with the ID field value.
     * 
     * @param payload a JSON or XML payload with the resource ID field in it
     * @return true if a resource was deleted; false otherwise
     */
    public boolean delete(String payload);

    /**
     * Deletes a resource with the given resource ID field value.
     * 
     * @param resourceIdFieldValue
     * @return true if a resource was deleted; false otherwise
     */
    public boolean deleteByIdField(String resourceIdFieldValue);
    
    /**
     * 
     * @return
     */
    public ResourcesFragment getAsXml();

    /**
     * 
     * @param resourceNameOrId
     * @return
     */
    public Fragment getAsXml(String resourceNameOrId);

    /**
     * 
     * @param resourceNameOrId
     * @return
     */
    public Fragment getPropertiesAsXml(String resourceNameOrId);
}
