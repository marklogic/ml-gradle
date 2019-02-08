package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.api.forest.ForestReplica;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupedReplicaBuilderStrategy extends ReplicaBuilderStrategy {

	/**
	 * This is the original ReplicaBuilderStrategy. The first replica for forest n on host h will be on host
	 * (h+1)%hostCount.
	 */
	public void buildReplicas(List<Forest> forests, ForestPlan forestPlan, AppConfig appConfig,
		List<String> dataDirectories, ForestNamingStrategy fns)
	{
		final String databaseName = forestPlan.getDatabaseName();
		final List<String> hostNames = forestPlan.getHostNames();
		final int replicaCount = forestPlan.getReplicaCount();

		if (appConfig.getReplicaForestDataDirectory() != null) {
			dataDirectories = new ArrayList<>();
			dataDirectories.add(appConfig.getReplicaForestDataDirectory());
		}
		Map<String, String> replicaDataDirectoryMap = appConfig.getDatabaseReplicaDataDirectories();
		if (replicaDataDirectoryMap != null && replicaDataDirectoryMap.containsKey(databaseName)) {
			dataDirectories = new ArrayList<>();
			dataDirectories.add(replicaDataDirectoryMap.get(databaseName));
		}

		for (Forest f : forests) {
			List<ForestReplica> replicas = new ArrayList<>();
			int hostPointer = hostNames.indexOf(f.getHost());
			int dataDirectoryPointer = dataDirectories.indexOf(f.getDataDirectory());
			for (int i = 1; i <= replicaCount; i++) {
				ForestReplica replica = new ForestReplica();
				replica.setReplicaName(fns.getReplicaName(databaseName, f.getForestName(), i, appConfig));
				replicas.add(replica);

				hostPointer++;
				if (hostPointer == hostNames.size()) {
					hostPointer = 0;
				}

				dataDirectoryPointer++;
				if (dataDirectoryPointer == dataDirectories.size()) {
					dataDirectoryPointer = 0;
				}

				replica.setHost(hostNames.get(hostPointer));

				final String dataDir = dataDirectories.get(dataDirectoryPointer);
				if (dataDir != null && dataDir.trim().length() > 0) {
					replica.setDataDirectory(dataDir);
				}

				configureReplica(replica, databaseName, appConfig);
			}

			f.setForestReplica(replicas);
		}

	}
}
