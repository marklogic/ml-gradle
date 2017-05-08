package com.marklogic.mgmt;

import com.marklogic.rest.util.Fragment;
import com.marklogic.rest.util.ResourcesFragment;

/**
 * Interface that AbstractResourceCommand is bound to. If you can implement this - most likely by subclassing
 * AbstractResourceManager - then you should be able to subclass AbstractResourceCommand in order to very easily
 * implement a new command.
 */
public interface ResourceManager {

    /**
     * @param resourceNameOrId
     * @param resourceUrlParams
     *            Some resources require URL parameters to uniquely identify them
     * @return true if a resource with the given name or ID exists, false otherwise
     */
    public boolean exists(String resourceNameOrId, String... resourceUrlParams);

    /**
     * If a resource with a name in the the given payload exists, then update that resource; else create it.
     * 
     * @param payload
     */
    public SaveReceipt save(String payload);

    /**
     * Assumes that a resource ID field is in the payload, and then extracts that field value and tries to delete a
     * resource with the ID field value.
     * 
     * @param payload
     *            a JSON or XML payload with the resource ID field in it
     * @param resourceUrlParams
     *            Some resources require URL parameters to uniquely identify them
     * @return true if a resource was deleted; false otherwise
     */
    public DeleteReceipt delete(String payload, String... resourceUrlParams);

    /**
     * Deletes a resource with the given resource ID field value.
     * 
     * @param resourceIdFieldValue
     * @param resourceUrlParams
     *            Some resources require URL parameters to uniquely identify them
     * @return true if a resource was deleted; false otherwise
     */
    public DeleteReceipt deleteByIdField(String resourceIdFieldValue, String... resourceUrlParams);

    /**
     * @return a ResourcesFragment instance containing the XML returned by the endpoint that lists all of the resources
     */
    public ResourcesFragment getAsXml();

    /**
     * @param resourceNameOrId
     * @param resourceUrlParams
     *            Some resources require URL parameters to uniquely identify them
     * @return
     */
    public Fragment getAsXml(String resourceNameOrId, String... resourceUrlParams);

    /**
     * 
     * @param resourceNameOrId
     * @param resourceUrlParams
     *            Some resources require URL parameters to uniquely identify them
     * @return
     */
    public String getAsJson(String resourceNameOrId, String... resourceUrlParams);

    /**
     * 
     * @param resourceNameOrId
     * @param resourceUrlParams
     *            Some resources require URL parameters to uniquely identify them
     * @return
     */
    public Fragment getPropertiesAsXml(String resourceNameOrId, String... resourceUrlParams);

	/**
	 *
	 * @param resourceNameOrId
	 * @param resourceUrlParams
	 *            Some resources require URL parameters to uniquely identify them
	 * @return
	 */
	public String getPropertiesAsXmlString(String resourceNameOrId, String... resourceUrlParams);

	/**
	 * @param resourceNameOrId
	 * @param resourceUrlParams
	 * @return
	 */
	public String getPropertiesAsJson(String resourceNameOrId, String... resourceUrlParams);

}
