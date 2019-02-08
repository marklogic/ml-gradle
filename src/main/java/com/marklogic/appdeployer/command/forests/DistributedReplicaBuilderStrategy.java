package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.api.forest.ForestReplica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistributedReplicaBuilderStrategy extends ReplicaBuilderStrategy {

	/**
	 * Distributes the replicas throughout the cluster.
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

		HashMap<String, List<Forest>> hostToForests = new HashMap<String, List<Forest>>();

		for (Forest f : forests) {
			String host = f.getHost();
			if (hostToForests.containsKey(host)) {
				hostToForests.get(host).add(f);
			}
			else {
				ArrayList<Forest> hostForests = new ArrayList<Forest>();
				hostForests.add(f);
				hostToForests.put(host, hostForests);
			}
		}

		for (String host : hostToForests.keySet()) {

			// availableHosts will be the hosts that we can put a forest's replicas on, which excludes the host where
			// the forest lives. We also want to have the hosts in different order as we assign replicas to hosts, so
			// that we don't overload any of them. So if we have five hosts, and we're looking to build replicas for
			// the forests on host 2, this list will be [host3, host4, host5, host1].
			List<String> availableHosts = new ArrayList<String>();
			int hostIndex = hostNames.indexOf(host);
			if (hostIndex != -1 && hostIndex < hostNames.size()) {
				availableHosts.addAll(hostNames.subList(hostIndex + 1, hostNames.size()));
			}
			availableHosts.addAll(hostNames.subList(0, hostIndex));

			int hostPointer = 0;

			for (Forest currForest : hostToForests.get(host)) {
				List<ForestReplica> replicas = new ArrayList<>();
				int dataDirectoryPointer = dataDirectories.indexOf(currForest.getDataDirectory());

				for (int i = 1; i <= replicaCount; i++) {
					ForestReplica replica = new ForestReplica();
					replica.setReplicaName(fns.getReplicaName(databaseName, currForest.getForestName(), i, appConfig));
					replicas.add(replica);

					int replicaHostPointer = hostPointer + i - 1;
					if (replicaHostPointer == availableHosts.size()) {
						replicaHostPointer = 0;
					}

					replica.setHost(availableHosts.get(replicaHostPointer));

					dataDirectoryPointer++;
					if (dataDirectoryPointer == dataDirectories.size()) {
						dataDirectoryPointer = 0;
					}

					final String dataDir = dataDirectories.get(dataDirectoryPointer);
					if (dataDir != null && dataDir.trim().length() > 0) {
						replica.setDataDirectory(dataDir);
					}

					configureReplica(replica, databaseName, appConfig);
				}

				currForest.setForestReplica(replicas);

				++hostPointer;

				if (hostPointer == availableHosts.size()) {
					hostPointer = 0;
				}

			}
		}

	}
}
