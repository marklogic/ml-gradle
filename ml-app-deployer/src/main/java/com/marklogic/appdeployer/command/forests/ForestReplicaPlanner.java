/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.forests;

import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.api.forest.ForestReplica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ForestReplicaPlanner {

	private static final Logger logger = LoggerFactory.getLogger(ForestReplicaPlanner.class);

	static class Host {
		String name;
		String zone;
		List<Forest> forests;

		Host(String hostName, String zone, List<Forest> forests) {
			this.name = hostName;
			this.zone = zone;
			this.forests = forests;
		}

		Host(String hostName, String zone, String... forests) {
			this.name = hostName;
			this.zone = zone;
			this.forests = new ArrayList<>();
			for (String forest : forests) {
				this.forests.add(new Forest(hostName, forest));
			}
		}

		@Override
		public String toString() {
			return "[Host: %s; zone: %s]".formatted(name, zone);
		}
	}

	static class ReplicaAssignment {
		String forest;
		String originalHost;
		List<String> replicaHosts;

		ReplicaAssignment(String forest, String originalHost) {
			this.forest = forest;
			this.originalHost = originalHost;
			this.replicaHosts = new ArrayList<>();
		}

		void addReplicaHost(String host) {
			replicaHosts.add(host);
		}
	}

	/**
	 * @param forestHosts       list of hosts containing primary forests.
	 * @param allAvailableHosts will differ from forestHosts in a scenario where e.g. forests are on a single host,
	 *                          which is common for modules/schemas databases.
	 * @param replicaCount      number of replica forests to create for each primary forest.
	 * @return
	 */
	static List<ReplicaAssignment> assignReplicas(List<Host> forestHosts, List<Host> allAvailableHosts, int replicaCount) {
		final List<Host> replicaHosts = allAvailableHosts;
		final boolean ignoreZones = shouldIgnoreZones(replicaHosts);

		int differentZoneIndex = 0;
		int sameZoneIndex = 0;
		final List<ReplicaAssignment> assignments = new ArrayList<>();

		for (final Host forestHost : forestHosts) {
			int forestIndex = 0;
			for (Forest forest : forestHost.forests) {
				ReplicaAssignment assignment = new ReplicaAssignment(forest.getForestName(), forestHost.name);
				List<Host> eligibleHosts = buildEligibleHostsList(forestHost, replicaHosts, ignoreZones);

				if (ignoreZones) {
					assignReplicasIgnoringZones(assignment, eligibleHosts, replicaCount, forestIndex);
				} else {
					assignReplicasWithZoneAwareness(assignment, eligibleHosts, forestHost, replicaCount, differentZoneIndex, sameZoneIndex);
					differentZoneIndex += replicaCount;
					sameZoneIndex += replicaCount;
				}

				addReplicasToForest(forest, assignment);
				assignments.add(assignment);
				forestIndex++;
			}
		}

		return assignments;
	}

	private static boolean shouldIgnoreZones(List<Host> replicaHosts) {
		return replicaHosts.stream().anyMatch(h -> h.zone == null);
	}

	private static List<Host> buildEligibleHostsList(Host sourceHost, List<Host> replicaHosts, boolean ignoreZones) {
		List<Host> eligibleHosts = new ArrayList<>();
		if (ignoreZones) {
			int sourceIndex = replicaHosts.indexOf(sourceHost);
			for (int i = 1; i < replicaHosts.size(); i++) {
				Host candidate = replicaHosts.get((sourceIndex + i) % replicaHosts.size());
				eligibleHosts.add(candidate);
			}
		} else {
			eligibleHosts = replicaHosts.stream()
				.filter(h -> !h.name.equals(sourceHost.name))
				.toList();
		}
		return eligibleHosts;
	}

	private static void assignReplicasIgnoringZones(ReplicaAssignment assignment, List<Host> eligibleHosts, int replicaCount, int forestIndex) {
		if (logger.isDebugEnabled()) {
			logger.debug("Assigning replicas without considering host zones.");
		}
		Set<String> usedHosts = new HashSet<>();
		for (int i = 0; i < replicaCount && i < eligibleHosts.size(); i++) {
			Host targetHost = eligibleHosts.get((forestIndex + i) % eligibleHosts.size());
			if (!usedHosts.contains(targetHost.name)) {
				assignment.addReplicaHost(targetHost.name);
				usedHosts.add(targetHost.name);
			}
		}
	}

	private static void assignReplicasWithZoneAwareness(ReplicaAssignment assignment, List<Host> eligibleHosts, Host sourceHost, int replicaCount, int differentZoneIndex, int sameZoneIndex) {
		if (logger.isDebugEnabled()) {
			logger.debug("Assigning replicas while taking host zones into account.");
		}

		ZoneHosts zoneHosts = separateHostsByZone(eligibleHosts, sourceHost);

		int replicasAssigned = 0;
		// Try to assign to hosts in different zones first.
		replicasAssigned = assignFromHostList(assignment, zoneHosts.differentZoneHosts, replicaCount, differentZoneIndex, replicasAssigned);
		// If we still need more replicas, use hosts in the same zone.
		assignFromHostList(assignment, zoneHosts.sameZoneHosts, replicaCount, sameZoneIndex, replicasAssigned);
	}

	private static ZoneHosts separateHostsByZone(List<Host> eligibleHosts, Host sourceHost) {
		List<Host> differentZoneHosts = new ArrayList<>();
		List<Host> sameZoneHosts = new ArrayList<>();
		for (Host h : eligibleHosts) {
			if (h.zone.equals(sourceHost.zone)) {
				sameZoneHosts.add(h);
			} else {
				differentZoneHosts.add(h);
			}
		}
		return new ZoneHosts(differentZoneHosts, sameZoneHosts);
	}

	private static int assignFromHostList(ReplicaAssignment assignment, List<Host> hostList, int replicaCount, int startIndex, int replicasAssigned) {
		Set<String> usedHosts = new HashSet<>();
		while (replicasAssigned < replicaCount && !hostList.isEmpty() && usedHosts.size() < hostList.size()) {
			Host targetHost = hostList.get(startIndex % hostList.size());
			startIndex++;
			if (!usedHosts.contains(targetHost.name)) {
				assignment.addReplicaHost(targetHost.name);
				usedHosts.add(targetHost.name);
				replicasAssigned++;
			}
		}
		return replicasAssigned;
	}

	private static void addReplicasToForest(Forest forest, ReplicaAssignment assignment) {
		forest.setForestReplica(new ArrayList<>());
		assignment.replicaHosts.forEach(replicaHost -> {
			ForestReplica replica = new ForestReplica();
			replica.setHost(replicaHost);
			forest.getForestReplica().add(replica);
		});
	}

	private static class ZoneHosts {
		final List<Host> differentZoneHosts;
		final List<Host> sameZoneHosts;

		ZoneHosts(List<Host> differentZoneHosts, List<Host> sameZoneHosts) {
			this.differentZoneHosts = differentZoneHosts;
			this.sameZoneHosts = sameZoneHosts;
		}
	}
}
