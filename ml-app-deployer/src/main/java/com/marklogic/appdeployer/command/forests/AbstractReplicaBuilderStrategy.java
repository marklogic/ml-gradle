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
package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.api.forest.ForestReplica;

import java.util.Map;

public abstract class AbstractReplicaBuilderStrategy extends LoggingObject implements ReplicaBuilderStrategy {

	/**
	 * Configures the fast and large data directories for a replica based on what's in AppConfig for the given
	 * database.
	 *
	 * @param replica
	 * @param databaseName
	 * @param appConfig
	 */
	protected void configureReplica(ForestReplica replica, String databaseName, AppConfig appConfig) {
		// First set to the database-agnostic forest directories
		replica.setFastDataDirectory(appConfig.getForestFastDataDirectory());
		replica.setLargeDataDirectory(appConfig.getForestLargeDataDirectory());

		// Now set to the database-specific forest directories if set
		if (databaseName != null) {
			Map<String, String> map = appConfig.getDatabaseFastDataDirectories();
			if (map != null && map.containsKey(databaseName)) {
				replica.setFastDataDirectory(map.get(databaseName));
			}
			map = appConfig.getDatabaseLargeDataDirectories();
			if (map != null && map.containsKey(databaseName)) {
				replica.setLargeDataDirectory(map.get(databaseName));
			}
		}

		// Now set to the replica forest directories if set
		if (appConfig.getReplicaForestFastDataDirectory() != null) {
			replica.setFastDataDirectory(appConfig.getReplicaForestFastDataDirectory());
		}
		if (appConfig.getReplicaForestLargeDataDirectory() != null) {
			replica.setLargeDataDirectory(appConfig.getReplicaForestLargeDataDirectory());
		}

		// And now set to the database-specific replica forest directories if set
		if (databaseName != null) {
			Map<String, String> map = appConfig.getDatabaseReplicaFastDataDirectories();
			if (map != null && map.containsKey(databaseName)) {
				replica.setFastDataDirectory(map.get(databaseName));
			}
			map = appConfig.getDatabaseReplicaLargeDataDirectories();
			if (map != null && map.containsKey(databaseName)) {
				replica.setLargeDataDirectory(map.get(databaseName));
			}
		}
	}


}
