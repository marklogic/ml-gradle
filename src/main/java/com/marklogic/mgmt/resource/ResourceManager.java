/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.mgmt.resource;

import com.marklogic.mgmt.DeleteReceipt;
import com.marklogic.mgmt.SaveReceipt;
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
