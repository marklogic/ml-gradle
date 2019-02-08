package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.mgmt.api.forest.ForestReplica;

import java.util.Map;

public abstract class AbstractReplicaBuilderStrategy implements ReplicaBuilderStrategy {

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
