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
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.api.forest.ForestReplica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DistributedReplicaBuilderStrategy extends AbstractReplicaBuilderStrategy {

	/**
	 * Distributes the replicas throughout the cluster.
	 */
	public void buildReplicas(List<Forest> forests, ForestPlan forestPlan, AppConfig appConfig,
		List<String> replicaDataDirectories, ForestNamingStrategy fns)
	{
		final String databaseName = forestPlan.getDatabaseName();
		final List<String> hostNames = forestPlan.getReplicaHostNames();
		final int replicaCount = forestPlan.getReplicaCount();

		HashMap<String, List<Forest>> hostToForests = new HashMap<>();

		for (Forest f : forests) {
			String host = f.getHost();
			if (hostToForests.containsKey(host)) {
				hostToForests.get(host).add(f);
			}
			else {
				ArrayList<Forest> hostForests = new ArrayList<>();
				hostForests.add(f);
				hostToForests.put(host, hostForests);
			}
		}

		for (String host : hostToForests.keySet()) {
			logger.info("Determining replicas for host: " + host);

			// availableHosts will be the hosts that we can put a forest's replicas on, which excludes the host where
			// the forest lives. We also want to have the hosts in different order as we assign replicas to hosts, so
			// that we don't overload any of them. So if we have five hosts, and we're looking to build replicas for
			// the forests on host 2, this list will be [host3, host4, host5, host1].
			List<String> availableHosts = new ArrayList<>();
			int hostIndex = hostNames.indexOf(host);
			if (hostIndex != -1 && hostIndex < hostNames.size()) {
				availableHosts.addAll(hostNames.subList(hostIndex + 1, hostNames.size()));
			}
			availableHosts.addAll(hostNames.subList(0, hostIndex));
			final int availableHostCount = availableHosts.size();
			logger.info("Available hosts for replicas: " + availableHosts);

			int hostPointer = 0;

			for (Forest currForest : hostToForests.get(host)) {
				List<ForestReplica> replicas = new ArrayList<>();
				int dataDirectoryPointer = replicaDataDirectories.indexOf(currForest.getDataDirectory());

				for (int i = 1; i <= replicaCount; i++) {
					ForestReplica replica = new ForestReplica();
					replica.setReplicaName(fns.getReplicaName(databaseName, currForest.getForestName(), i, appConfig));
					replicas.add(replica);

					int replicaHostPointer = hostPointer + i - 1;
					if (replicaHostPointer >= availableHostCount) {
						// Must do a modulo here in the event that there are more primary forests per host than number of hosts
						replicaHostPointer %= availableHostCount;
					}
					replica.setHost(availableHosts.get(replicaHostPointer));
					logger.info(format("Built replica '%s' for forest '%s' on host '%s'", replica.getReplicaName(), currForest.getForestName(), replica.getHost()));

					dataDirectoryPointer++;
					if (dataDirectoryPointer == replicaDataDirectories.size()) {
						dataDirectoryPointer = 0;
					}

					final String dataDir = replicaDataDirectories.get(dataDirectoryPointer);
					if (dataDir != null && dataDir.trim().length() > 0) {
						replica.setDataDirectory(dataDir);
					}

					configureReplica(replica, databaseName, appConfig);
				}

				currForest.setForestReplica(replicas);

				++hostPointer;

				if (hostPointer == availableHostCount) {
					hostPointer = 0;
				}

			}
		}

	}
}
