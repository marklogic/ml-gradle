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
package com.marklogic.mgmt.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.mgmt.DeleteReceipt;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Base class for any class that we both want to read/write from/to JSON and make calls to the Management REST API.
 */
public abstract class Resource extends ApiObject {

    private Logger logger;

    private API api;

	/**
	 * This constructor can be used when a client doesn't to perform any operations with the Manage API, but rather
	 * just wants to use the subclass instance like a regular Java bean class.
	 */
	protected Resource() {
		setObjectMapper(ObjectMapperFactory.getObjectMapper());
    }

	/**
	 *
	 * @param api
	 */
	protected Resource(API api) {
        this.api = api;
        if (api != null) {
	        setObjectMapper(api.getObjectMapper());
        } else {
        	setObjectMapper(ObjectMapperFactory.getObjectMapper());
        }
    }

    protected Logger getLogger() {
    	if (logger == null) {
    		logger = LoggerFactory.getLogger(getClass());
	    }
	    return logger;
    }

	/**
	 *
	 * @return
	 */
	public ObjectNode toObjectNode() {
		// Haven't found a better way than serializing this out to a String and then reading back in as an ObjectNode
		String json = getJson();
		try {
			return (ObjectNode)ObjectMapperFactory.getObjectMapper().readTree(json);
		} catch (IOException e) {
			throw new RuntimeException("Unable to convert to ObjectNode, cause: " + e.getMessage(), e);
		}
	}


	/**
     * @return a receipt string containing the path and HTTP status code
     */
    public String save() {
        String name = getResourceType();
        String label = getResourceLabel();
        if (getLogger().isInfoEnabled()) {
            getLogger().info(format("Saving %s %s", name, label));
        }
        SaveReceipt receipt = getResourceManager().save(getJson());
        if (getLogger().isInfoEnabled()) {
            getLogger().info(format("Saved %s %s", name, label));
        }
        return format("[Path: %s; Resource ID: %s; HTTP status: %s]", receipt.getPath(), receipt.getResourceId(),
                receipt.getResponse() != null ? receipt.getResponse().getStatusCode() : "(none)");
    }

    /**
     * @return a receipt with the path (if the resource was found), the resource ID, and whether the resource was
     *         deleted
     */
    public String delete() {
        String name = getResourceType();
        String label = getResourceLabel();
        if (getLogger().isInfoEnabled()) {
            getLogger().info(format("Deleting %s %s", name, label));
        }
        DeleteReceipt receipt = getResourceManager().deleteByIdField(getResourceId(), getResourceUrlParams());
        if (getLogger().isInfoEnabled()) {
            getLogger().info(format("Deleted %s %s", name, label));
        }
        return receipt.isDeleted() ? format("[Path: %s; Resource ID: %s; deleted: true]", receipt.getPath(),
                receipt.getResourceId()) : format("[Resource ID: %s; deleted: false]", receipt.getResourceId());
    }

    /**
     * TODO Not totally convinced about putting this method here, as it means that an instance of this is needed to get
     * a list of names. The other choices would be a method on the API class, but then that means the API class needs a
     * method per resource for getting a list of names, whereas with this approach, we have a single method.
     *
     * @return a list of names of all resources of this type.
     */
    public List<String> list() {
        List<String> list = getResourceManager().getAsXml().getListItemNameRefs();
        Collections.sort(list);
        return list;
    }

    /**
     * @return true of the resource exists, false otherwise
     */
    public boolean exists() {
        return getResourceManager().exists(getResourceId(), getResourceUrlParams());
    }

    /**
     * Some resources, such as amps, require additional parameters in the URL to uniquely identify the resource. A
     * subclass can override this to provide those parameters.
     *
     * @return
     */
    @JsonIgnore
    public String[] getResourceUrlParams() {
        return null;
    }

    /**
     * @return a ResourceManager instance that will be used for the persistence methods in this class
     */
    protected abstract ResourceManager getResourceManager();

    /**
     * @return the unique identifier for this resource, which will be used for the persistence methods in this class
     */
    protected abstract String getResourceId();

    /**
     * @return a value that is a useful label for identifying this resource instance, which can be used for log
     *         messages. Defaults to getResourceId, as that's normally a good candidate for a label.
     */
    protected String getResourceLabel() {
        return getResourceId();
    }

    protected String getResourceType() {
        return getClass().getSimpleName().toLowerCase();
    }

    @JsonIgnore
    protected API getApi() {
        return api;
    }

    @JsonIgnore
    protected ManageClient getClient() {
        return api.getManageClient();
    }

    protected String format(String format, Object... args) {
        return String.format(format, args);
    }

    public void setApi(API api) {
        this.api = api;
    }

    @Override
    public String toString() {
        return format("[%s: %s]", getResourceType(), getResourceLabel());
    }

}
