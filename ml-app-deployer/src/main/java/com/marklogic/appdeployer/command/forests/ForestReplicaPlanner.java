/*
 * Copyright © 2025 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.forests;

import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.api.forest.ForestReplica;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ForestReplicaPlanner {

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

    static List<ReplicaAssignment> assignReplicas(List<Host> hosts, int replicaCount) {
        return assignReplicas(hosts, replicaCount, null);
    }

    /**
     * @param hosts
     * @param replicaCount
     * @param allAvailableHosts is not null for a scenario where e.g. forests for a database are only created on one
     *                          host, such as for a modules or schemas database. In that scenario, the caller needs to
     *                          pass in a list of all available hosts in the cluster, so that replicas can be created
     *                          on those hosts.
     * @return
     */
    static List<ReplicaAssignment> assignReplicas(List<Host> hosts, int replicaCount, List<String> allAvailableHosts) {
        final List<Host> replicaHosts = buildReplicaHostsList(hosts, allAvailableHosts);
        final boolean ignoreZones = shouldIgnoreZones(replicaHosts);

        int differentZoneIndex = 0;
        int sameZoneIndex = 0;
        final List<ReplicaAssignment> assignments = new ArrayList<>();

        for (final Host host : hosts) {
            int forestIndex = 0;
            for (Forest forest : host.forests) {
                ReplicaAssignment assignment = new ReplicaAssignment(forest.getForestName(), host.name);
                List<Host> eligibleHosts = buildEligibleHostsList(host, replicaHosts, ignoreZones);

                if (ignoreZones) {
                    assignReplicasIgnoringZones(assignment, eligibleHosts, replicaCount, forestIndex);
                } else {
                    assignReplicasWithZoneAwareness(assignment, eligibleHosts, host, replicaCount, differentZoneIndex, sameZoneIndex);
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

    private static List<Host> buildReplicaHostsList(List<Host> hosts, List<String> allAvailableHosts) {
        List<Host> replicaHosts = new ArrayList<>(hosts);
        if (allAvailableHosts != null) {
            for (String availableHost : allAvailableHosts) {
                boolean hostAlreadyExists = hosts.stream().anyMatch(h -> h.name.equals(availableHost));
                if (!hostAlreadyExists) {
                    replicaHosts.add(new Host(availableHost, null, new ArrayList<>()));
                }
            }
        }
        return replicaHosts;
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
        List<Host> differentZoneHosts = new ArrayList<>();
        List<Host> sameZoneHosts = new ArrayList<>();

        for (Host h : eligibleHosts) {
            if (h.zone.equals(sourceHost.zone)) {
                sameZoneHosts.add(h);
            } else {
                differentZoneHosts.add(h);
            }
        }

        Set<String> usedHosts = new HashSet<>();
        int replicasAssigned = 0;

        // First, try to assign from different zones
        replicasAssigned = assignFromHostList(assignment, differentZoneHosts, replicaCount, differentZoneIndex, usedHosts, replicasAssigned);

        // If we still need more replicas, use same-zone hosts
        assignFromHostList(assignment, sameZoneHosts, replicaCount, sameZoneIndex, usedHosts, replicasAssigned);
    }

    private static int assignFromHostList(ReplicaAssignment assignment, List<Host> hostList, int replicaCount, int startIndex, Set<String> usedHosts, int replicasAssigned) {
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
}
