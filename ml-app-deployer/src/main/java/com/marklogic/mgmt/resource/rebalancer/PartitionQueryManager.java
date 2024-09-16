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

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.AbstractResourceManager;

public class PartitionQueryManager extends AbstractResourceManager {

	private String databaseIdOrName;

	/**
	 * The "update properties" endpoint for partition queries -
	 * http://docs.marklogic.com/REST/PUT/manage/v2/databases/[id-or-name]/partition-queries/[partition-number]/properties
	 * - only supports a couple inputs for affecting the status of a partition query. The inputs used for creating a
	 * partition query are not supported. Thus, the concept of "updates" in this class are not supported either.
	 *
	 * @param client
	 * @param databaseIdOrName
	 */
	public PartitionQueryManager(ManageClient client, String databaseIdOrName) {
		super(client);
		this.databaseIdOrName = databaseIdOrName;
		setUpdateAllowed(false);
	}

	@Override
	public String getResourcesPath() {
		return format("/manage/v2/databases/%s/partition-queries", databaseIdOrName);
	}

	@Override
	protected String getIdFieldName() {
		return "partition-number";
	}

}
