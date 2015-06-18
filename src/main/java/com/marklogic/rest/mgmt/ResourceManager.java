package com.marklogic.rest.mgmt;

import com.marklogic.rest.util.Fragment;

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
     * If a resource with a name in the the given JSON exists, then update that resource; else create it.
     * 
     * @param json
     */
    public void save(String json);

    /**
     * If a resource with the given name or ID exists, then delete it; else do nothing.
     * 
     * @param resourceNameOrId
     */
    public void delete(String resourceNameOrId);

    public Fragment getAsXml(String resourceNameOrId);
}
