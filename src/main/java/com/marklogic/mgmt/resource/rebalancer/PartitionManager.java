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
package com.marklogic.mgmt.resource.rebalancer;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public class PartitionManager extends AbstractResourceManager {

	private String databaseIdOrName;

	/**
	 * The "update properties" endpoint for partitions - http://docs.marklogic.com/REST/PUT/manage/v2/databases/[id-or-name]/partitions/[name]/properties
	 * - only supports a couple inputs for affecting the status of a partition. The inputs used for creating a
	 * partition are not supported. Thus, the concept of "updates" in this class are not supported either.
	 *
	 * @param client
	 * @param databaseIdOrName
	 */
	public PartitionManager(ManageClient client, String databaseIdOrName) {
		super(client);
		this.databaseIdOrName = databaseIdOrName;
		setUpdateAllowed(false);
	}

	@Override
	public String getResourcesPath() {
		return format("/manage/v2/databases/%s/partitions", databaseIdOrName);
	}

	@Override
	protected String getIdFieldName() {
		return "partition-name";
	}

	public ResponseEntity<String> takePartitionOnline(String partitionName) {
		return getManageClient().putJson(getPropertiesPath(partitionName), "{\"availability\":\"online\"}");
	}

	public ResponseEntity<String> takePartitionOffline(String partitionName) {
		return getManageClient().putJson(getPropertiesPath(partitionName), "{\"availability\":\"offline\"}");
	}

	public PartitionProperties getPartitionProperties(String partitionName) {
		JsonNode json = getManageClient().getJsonNode(getPropertiesPath(partitionName));
		if (json != null && json.has("partition-properties")) {
			JsonNode props = json.get("partition-properties");
			try {
				return ObjectMapperFactory.getObjectMapper().readerFor(PartitionProperties.class).readValue(props.toString());
			} catch (IOException e) {
				throw new RuntimeException("Unable to unmarshal partition properties JSON response: " + json, e);
			}
		} else {
			throw new RuntimeException("Unexpected payload for partition properties, could not find " +
				"partition-properties key; payload; " + json);
		}
	}
}
