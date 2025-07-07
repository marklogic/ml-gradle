/*
 * Copyright © 2025 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.api.forest.ForestReplica;

import java.util.*;

/**
 * Temporarily using this for the logic in MLE-20741. Will eventually replace DistributedReplicaBuilderStrategy with
 * this, once we know this is all working properly.
 */
class ZoneAwareReplicaBuilderStrategy extends AbstractReplicaBuilderStrategy {

	@Override
	public void buildReplicas(List<Forest> forests, ForestPlan forestPlan, AppConfig appConfig,
							  List<String> replicaDataDirectories, ForestNamingStrategy forestNamingStrategy) {
		Map<String, List<Forest>> hostToForests = new LinkedHashMap<>();
		for (Forest f : forests) {
			String host = f.getHost();
			if (hostToForests.containsKey(host)) {
				hostToForests.get(host).add(f);
			} else {
				List<Forest> hostForests = new ArrayList<>();
				hostForests.add(f);
				hostToForests.put(host, hostForests);
			}
		}

		final List<ForestReplicaPlanner.Host> hosts = new ArrayList<>();
		hostToForests.forEach((host, hostForests) -> {
			final String zone = forestPlan.getHostsToZones() != null ? forestPlan.getHostsToZones().get(host) : null;
			hosts.add(new ForestReplicaPlanner.Host(host, zone, hostForests));
		});

		List<ForestReplicaPlanner.Host> allAvailableHosts = new ArrayList<>();
		for (String hostName : forestPlan.getHostNames()) {
			final String zone = forestPlan.getHostsToZones() != null ? forestPlan.getHostsToZones().get(hostName) : null;
			allAvailableHosts.add(new ForestReplicaPlanner.Host(hostName, zone));
		}

		ForestReplicaPlanner.assignReplicas(hosts, forestPlan.getReplicaCount(), allAvailableHosts);

		for (Forest forest : forests) {
			if (forest.getForestReplica() == null) {
				continue;
			}
			DataDirectoryIterator dataDirectoryIterator = new DataDirectoryIterator(replicaDataDirectories);
			for (int i = 0; i < forest.getForestReplica().size(); i++) {
				ForestReplica replica = forest.getForestReplica().get(i);
				String replicaName = forestNamingStrategy.getReplicaName(forestPlan.getDatabaseName(), forest.getForestName(), i + 1, appConfig);
				replica.setReplicaName(replicaName);
				replica.setDataDirectory(dataDirectoryIterator.next());
				configureReplica(replica, forestPlan.getDatabaseName(), appConfig);
			}
		}
	}

	private static class DataDirectoryIterator implements Iterator<String> {

		private final List<String> dataDirectories;
		private int index = 0;

		public DataDirectoryIterator(List<String> dataDirectories) {
			this.dataDirectories = dataDirectories;
		}

		@Override
		public boolean hasNext() {
			return true;
		}

		@Override
		public String next() {
			String value = dataDirectories.get(index);
			index = (index + 1) % dataDirectories.size();
			return value;
		}
	}
}
