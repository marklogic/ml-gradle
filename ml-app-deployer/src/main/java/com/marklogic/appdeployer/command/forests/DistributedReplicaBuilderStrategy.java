/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.api.forest.ForestReplica;

import java.util.*;

/**
 * Rewritten in 6.0.0 - largely by Copilot - to account for host zones, which is largely handled by {@code ForestReplicaPlanner}.
 */
class DistributedReplicaBuilderStrategy extends AbstractReplicaBuilderStrategy {

	@Override
	public void buildReplicas(List<Forest> forests, ForestPlan forestPlan, AppConfig appConfig,
							  List<String> replicaDataDirectories, ForestNamingStrategy forestNamingStrategy) {

		final Map<String, List<Forest>> hostNamesToForests = mapHostNamesToForests(forests);
		final List<ForestReplicaPlanner.Host> forestHosts = buildForestHosts(hostNamesToForests, forestPlan);
		final List<ForestReplicaPlanner.Host> allAvailableHosts = buildAllAvailableHosts(hostNamesToForests, forestHosts, forestPlan);

		ForestReplicaPlanner.assignReplicas(forestHosts, allAvailableHosts, forestPlan.getReplicaCount());
		configureReplicas(forests, forestPlan.getDatabaseName(), appConfig, replicaDataDirectories, forestNamingStrategy);
	}

	private Map<String, List<Forest>> mapHostNamesToForests(List<Forest> forests) {
		Map<String, List<Forest>> hostsToForests = new LinkedHashMap<>();
		for (Forest f : forests) {
			String host = f.getHost();
			if (hostsToForests.containsKey(host)) {
				hostsToForests.get(host).add(f);
			} else {
				List<Forest> hostForests = new ArrayList<>();
				hostForests.add(f);
				hostsToForests.put(host, hostForests);
			}
		}
		return hostsToForests;
	}

	/**
	 * Build a list of the Host objects as defined by ForestReplicaPlanner.
	 */
	private List<ForestReplicaPlanner.Host> buildForestHosts(Map<String, List<Forest>> hostToForests, ForestPlan forestPlan) {
		final List<ForestReplicaPlanner.Host> forestHosts = new ArrayList<>();
		hostToForests.forEach((host, hostForests) -> {
			final String zone = forestPlan.getHostsToZones() != null ? forestPlan.getHostsToZones().get(host) : null;
			forestHosts.add(new ForestReplicaPlanner.Host(host, zone, hostForests));
		});
		return forestHosts;
	}

	/**
	 * Build a list of all available hosts, starting with the hosts that contain primary forests. The main reason this
	 * list will differ from the list of forest hosts is for a database that has its primary forests on a single host.
	 * This is common for modules/schemas databases. In that scenario, we need the list of all available hosts so that
	 * replicas can be assigned to the other hosts.
	 */
	private List<ForestReplicaPlanner.Host> buildAllAvailableHosts(Map<String, List<Forest>> hostNamesToForests,
																   List<ForestReplicaPlanner.Host> forestHosts, ForestPlan forestPlan) {
		List<ForestReplicaPlanner.Host> allAvailableHosts = new ArrayList<>(forestHosts);
		for (String hostName : forestPlan.getHostNames()) {
			if (!hostNamesToForests.containsKey(hostName)) {
				final String zone = forestPlan.getHostsToZones() != null ? forestPlan.getHostsToZones().get(hostName) : null;
				allAvailableHosts.add(new ForestReplicaPlanner.Host(hostName, zone));
			}
		}
		return allAvailableHosts;
	}

	/**
	 * Once replicas have been built, configure their properties. This is independent of the replica assignment
	 * process.
	 */
	private void configureReplicas(List<Forest> forests, String databaseName, AppConfig appConfig,
								   List<String> replicaDataDirectories, ForestNamingStrategy forestNamingStrategy) {
		for (Forest forest : forests) {
			if (forest.getForestReplica() == null) {
				continue;
			}
			DataDirectoryIterator dataDirectoryIterator = new DataDirectoryIterator(replicaDataDirectories);
			for (int i = 0; i < forest.getForestReplica().size(); i++) {
				ForestReplica replica = forest.getForestReplica().get(i);
				String replicaName = forestNamingStrategy.getReplicaName(databaseName, forest.getForestName(), i + 1, appConfig);
				replica.setReplicaName(replicaName);
				replica.setDataDirectory(dataDirectoryIterator.next());
				configureReplica(replica, databaseName, appConfig);
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
