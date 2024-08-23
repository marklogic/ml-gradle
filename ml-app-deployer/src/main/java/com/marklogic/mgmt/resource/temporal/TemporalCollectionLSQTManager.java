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
package com.marklogic.mgmt.resource.temporal;

import com.marklogic.mgmt.AbstractManager;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.rest.util.ResourcesFragment;

/**
 * Can't extend {@link AbstractResourceManager} as LSQT is represented as properties of a temporal
 * collection, rather than a resource in itself.
 *
 * Uses the following REST Endpoint -
 * /manage/v2/databases/{id|name}/temporal/collections/lsqt/properties?collection={name} (GET/PUT)
 *
 * Created by dsmyth on 27/02/2017.
 */
public class TemporalCollectionLSQTManager extends AbstractManager {

	private String databaseIdOrName;
	private String temporalCollectionName;

	private ManageClient manageClient;

	public TemporalCollectionLSQTManager(ManageClient client, String databaseIdOrName, String temporalCollectionName) {
		this.manageClient = client;
		this.databaseIdOrName = databaseIdOrName;
		this.temporalCollectionName = temporalCollectionName;
	}

	/**
	 * @return the resources path for the temporal collections in the databaseIdOrName specified in the constructor
	 */
	private String getResourcesPath() {
		return format("/manage/v2/databases/%s/temporal/collections", databaseIdOrName);
	}

	public String getPropertiesPath() {
		return format("%s/lsqt/properties?collection=%s", getResourcesPath(),temporalCollectionName );
	}

	public boolean isTemporalCollectionExists() {
		ResourcesFragment temporalCollections = new ResourcesFragment(manageClient.getXml(getResourcesPath()));
		return temporalCollections.resourceExists(temporalCollectionName);
	}


	public void save(String payload) {
		if (isTemporalCollectionExists()) {
			String path = getPropertiesPath();
			logger.info(format("Updating LSQT properties for %s temporal collection", temporalCollectionName));
			putPayload(manageClient, path, payload);
			logger.info(format("Updated LSQT properties for %s temporal collection", temporalCollectionName));
		} else {
			logger.warn(format("Temporal collection %s not found. No update to LSQT settings applied", temporalCollectionName));
		}
	}

	public ManageClient getManageClient() {
		return manageClient;
	}

}
