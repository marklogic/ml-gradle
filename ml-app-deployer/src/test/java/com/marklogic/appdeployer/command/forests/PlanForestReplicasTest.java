/*
 * Copyright © 2025 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.forests;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlanForestReplicasTest {

	@Test
	void threeHostsOneForestOneReplicaNoZone() {
		List<ForestReplicaPlanner.Host> hosts = Arrays.asList(
			new ForestReplicaPlanner.Host("host1", null, "forest1"),
			new ForestReplicaPlanner.Host("host2", null, "forest2"),
			new ForestReplicaPlanner.Host("host3", null, "forest3")
		);

		List<ForestReplicaPlanner.ReplicaAssignment> results = ForestReplicaPlanner.assignReplicas(hosts, 1);
		assertEquals(3, results.size());

		verifyAssignment(results.get(0), "host2");
		verifyAssignment(results.get(1), "host3");
		verifyAssignment(results.get(2), "host1");
	}

	@Test
	void threeHostsTwoForestsOneReplicaNoZone() {
		List<ForestReplicaPlanner.Host> hosts = Arrays.asList(
			new ForestReplicaPlanner.Host("host1", null, "forest1", "forest2"),
			new ForestReplicaPlanner.Host("host2", null, "forest3", "forest4"),
			new ForestReplicaPlanner.Host("host3", null, "forest5", "forest6")
		);

		List<ForestReplicaPlanner.ReplicaAssignment> results = ForestReplicaPlanner.assignReplicas(hosts, 1);
		assertEquals(6, results.size());

		verifyAssignment(results.get(0), "host2");
		verifyAssignment(results.get(1), "host3");
		verifyAssignment(results.get(2), "host3");
		verifyAssignment(results.get(3), "host1");
		verifyAssignment(results.get(4), "host1");
		verifyAssignment(results.get(5), "host2");
	}

	@Test
	void threeHostsOneForestTwoReplicasNoZone() {
		List<ForestReplicaPlanner.Host> hosts = Arrays.asList(
			new ForestReplicaPlanner.Host("host1", null, "forest1"),
			new ForestReplicaPlanner.Host("host2", null, "forest2"),
			new ForestReplicaPlanner.Host("host3", null, "forest3")
		);

		List<ForestReplicaPlanner.ReplicaAssignment> results = ForestReplicaPlanner.assignReplicas(hosts, 2);
		assertEquals(3, results.size());

		verifyAssignment(results.get(0), "host2", "host3");
		verifyAssignment(results.get(1), "host3", "host1");
		verifyAssignment(results.get(2), "host1", "host2");
	}

	@Test
	void threeHostsTwoForestsTwoReplicasNoZone() {
		List<ForestReplicaPlanner.Host> hosts = Arrays.asList(
			new ForestReplicaPlanner.Host("host1", null, "forest1", "forest2"),
			new ForestReplicaPlanner.Host("host2", null, "forest3", "forest4"),
			new ForestReplicaPlanner.Host("host3", null, "forest5", "forest6")
		);

		List<ForestReplicaPlanner.ReplicaAssignment> results = ForestReplicaPlanner.assignReplicas(hosts, 2);
		assertEquals(6, results.size());

		// This shows how replicas are assigned in a round-robin fashion, with each forest starting with the next
		// eligible host. Replicas are then created starting with the eligible host and each eligible host after it.
		// Note that the starting point for replicas for the next forest is not based on where the last replica was
		// created for the previous forest, but rather based on the next eligible host in the list.
		verifyAssignment(results.get(0), "host2", "host3");
		verifyAssignment(results.get(1), "host3", "host2");
		verifyAssignment(results.get(2), "host3", "host1");
		verifyAssignment(results.get(3), "host1", "host3");
		verifyAssignment(results.get(4), "host1", "host2");
		verifyAssignment(results.get(5), "host2", "host1");
	}

	@Test
	void threeHostsOneReplicaThreeZones() {
		List<ForestReplicaPlanner.Host> hosts = Arrays.asList(
			new ForestReplicaPlanner.Host("host1", "zoneA", "forest1", "forest2"),
			new ForestReplicaPlanner.Host("host2", "zoneB", "forest3", "forest4"),
			new ForestReplicaPlanner.Host("host3", "zoneC", "forest5", "forest6")
		);

		List<ForestReplicaPlanner.ReplicaAssignment> results = ForestReplicaPlanner.assignReplicas(hosts, 1);
		assertEquals(6, results.size());

		verifyAssignment(results.get(0), "host2");
		verifyAssignment(results.get(1), "host3");
		verifyAssignment(results.get(2), "host1");
		verifyAssignment(results.get(3), "host3");
		verifyAssignment(results.get(4), "host1");
		verifyAssignment(results.get(5), "host2");
	}

	@Test
	void threeHostsTwoReplicasThreeZones() {
		List<ForestReplicaPlanner.Host> hosts = Arrays.asList(
			new ForestReplicaPlanner.Host("host1", "zoneA", "forest1", "forest2"),
			new ForestReplicaPlanner.Host("host2", "zoneB", "forest3", "forest4"),
			new ForestReplicaPlanner.Host("host3", "zoneC", "forest5", "forest6")
		);

		List<ForestReplicaPlanner.ReplicaAssignment> results = ForestReplicaPlanner.assignReplicas(hosts, 2);
		assertEquals(6, results.size());
		verifyAssignment(results.get(0), "host2", "host3");
		verifyAssignment(results.get(1), "host2", "host3");
		verifyAssignment(results.get(2), "host1", "host3");
		verifyAssignment(results.get(3), "host1", "host3");
		verifyAssignment(results.get(4), "host1", "host2");
		verifyAssignment(results.get(5), "host1", "host2");
	}

	@Test
	void oneZone() {
		List<ForestReplicaPlanner.Host> hosts = Arrays.asList(
			new ForestReplicaPlanner.Host("host1", "zoneA", "forest1", "forest2"),
			new ForestReplicaPlanner.Host("host2", "zoneA", "forest3", "forest4"),
			new ForestReplicaPlanner.Host("host3", "zoneA", "forest5", "forest6")
		);

		List<ForestReplicaPlanner.ReplicaAssignment> results = ForestReplicaPlanner.assignReplicas(hosts, 1);
		assertEquals(6, results.size());
		verifyAssignment(results.get(0), "host2");
		verifyAssignment(results.get(1), "host3");
		verifyAssignment(results.get(2), "host1");
		verifyAssignment(results.get(3), "host3");
		verifyAssignment(results.get(4), "host1");
		verifyAssignment(results.get(5), "host2");
	}

	@Test
	void oneZoneSixHostsTwoReplicas() {
		List<ForestReplicaPlanner.Host> hosts = Arrays.asList(
			new ForestReplicaPlanner.Host("host1", "zoneA", "forest1", "forest2"),
			new ForestReplicaPlanner.Host("host2", "zoneA", "forest3", "forest4"),
			new ForestReplicaPlanner.Host("host3", "zoneA", "forest5", "forest6"),
			new ForestReplicaPlanner.Host("host4", "zoneA", "forest7", "forest8"),
			new ForestReplicaPlanner.Host("host5", "zoneA", "forest9", "forest10"),
			new ForestReplicaPlanner.Host("host6", "zoneA", "forest11", "forest12")
		);

		List<ForestReplicaPlanner.ReplicaAssignment> results = ForestReplicaPlanner.assignReplicas(hosts, 2);
		assertEquals(12, results.size());

		// Host1 forests
		verifyAssignment(results.get(0), "host2", "host3");
		verifyAssignment(results.get(1), "host4", "host5");

		// Host2 forests
		verifyAssignment(results.get(2), "host6", "host1");
		verifyAssignment(results.get(3), "host3", "host4");

		// Host 3 forests
		verifyAssignment(results.get(4), "host5", "host6");
		verifyAssignment(results.get(5), "host1", "host2");

		// Host 4 forests
		verifyAssignment(results.get(6), "host3", "host5");
		verifyAssignment(results.get(7), "host6", "host1");

		// Host 5 forests
		verifyAssignment(results.get(8), "host2", "host3");
		verifyAssignment(results.get(9), "host4", "host6");

		// Host 6 forests
		verifyAssignment(results.get(10), "host1", "host2");
		verifyAssignment(results.get(11), "host3", "host4");
	}

	@Test
	void fourHostsInTwoZones() {
		List<ForestReplicaPlanner.Host> hosts = Arrays.asList(
			new ForestReplicaPlanner.Host("host1", "zoneA", "forest1", "forest2"),
			new ForestReplicaPlanner.Host("host2", "zoneA", "forest3", "forest4"),
			new ForestReplicaPlanner.Host("host3", "zoneB", "forest5", "forest6"),
			new ForestReplicaPlanner.Host("host4", "zoneB", "forest7", "forest8")
		);

		List<ForestReplicaPlanner.ReplicaAssignment> results = ForestReplicaPlanner.assignReplicas(hosts, 1);
		results.forEach(System.out::println);

		verifyAssignment(results.get(0), "host3");
		verifyAssignment(results.get(1), "host4");
		verifyAssignment(results.get(2), "host3");
		verifyAssignment(results.get(3), "host4");
		verifyAssignment(results.get(4), "host1");
		verifyAssignment(results.get(5), "host2");
		verifyAssignment(results.get(6), "host1");
		verifyAssignment(results.get(7), "host2");
	}

	@Test
	void sixHostsInThreeZones() {
		List<ForestReplicaPlanner.Host> hosts = Arrays.asList(
			new ForestReplicaPlanner.Host("host1", "zoneA", "forest1", "forest2", "forest3"),
			new ForestReplicaPlanner.Host("host2", "zoneA", "forest4", "forest5", "forest6"),
			new ForestReplicaPlanner.Host("host3", "zoneB", "forest7", "forest8", "forest9"),
			new ForestReplicaPlanner.Host("host4", "zoneB", "forest10", "forest11", "forest12"),
			new ForestReplicaPlanner.Host("host5", "zoneC", "forest13", "forest14", "forest15"),
			new ForestReplicaPlanner.Host("host6", "zoneC", "forest16", "forest17", "forest18")
		);

		List<ForestReplicaPlanner.ReplicaAssignment> results = ForestReplicaPlanner.assignReplicas(hosts, 1);
		assertEquals(18, results.size());

		results.forEach(System.out::println);

		// ZoneA forests
		verifyAssignment(results.get(0), "host3");
		verifyAssignment(results.get(1), "host4");
		verifyAssignment(results.get(2), "host5");
		verifyAssignment(results.get(3), "host6");
		verifyAssignment(results.get(4), "host3");
		verifyAssignment(results.get(5), "host4");

		// ZoneB forests
		verifyAssignment(results.get(6), "host5");
		verifyAssignment(results.get(7), "host6");
		verifyAssignment(results.get(8), "host1");
		verifyAssignment(results.get(9), "host2");
		verifyAssignment(results.get(10), "host5");
		verifyAssignment(results.get(11), "host6");

		// ZoneC forests
		verifyAssignment(results.get(12), "host1");
		verifyAssignment(results.get(13), "host2");
		verifyAssignment(results.get(14), "host3");
		verifyAssignment(results.get(15), "host4");
		verifyAssignment(results.get(16), "host1");
		verifyAssignment(results.get(17), "host2");
	}

	private void verifyAssignment(ForestReplicaPlanner.ReplicaAssignment assignment, String... expectedReplicaHosts) {
		assertEquals(expectedReplicaHosts.length, assignment.replicaHosts.size());
		for (int i = 0; i < assignment.replicaHosts.size(); i++) {
			assertEquals(expectedReplicaHosts[i], assignment.replicaHosts.get(i),
				"Unexpected replica host for: " + assignment);
		}
	}
}
