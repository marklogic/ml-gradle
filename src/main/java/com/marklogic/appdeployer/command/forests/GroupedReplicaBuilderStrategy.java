package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.api.forest.ForestReplica;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the original ReplicaBuilderStrategy. The first replica for forest n on host h will be on host
 * (h+1)%hostCount. The distributed strategy is preferred so that replicas for host h are distributed across all of the
 * hosts instead of all being on one other host.
 */
@Deprecated
public class GroupedReplicaBuilderStrategy extends AbstractReplicaBuilderStrategy {

	public void buildReplicas(List<Forest> forests, ForestPlan forestPlan, AppConfig appConfig,
	                          List<String> replicaDataDirectories, ForestNamingStrategy fns) {
		final String databaseName = forestPlan.getDatabaseName();
		final List<String> hostNames = forestPlan.getHostNames();
		final int replicaCount = forestPlan.getReplicaCount();

		for (Forest f : forests) {
			List<ForestReplica> replicas = new ArrayList<>();
			int hostPointer = hostNames.indexOf(f.getHost());
			int dataDirectoryPointer = replicaDataDirectories.indexOf(f.getDataDirectory());
			for (int i = 1; i <= replicaCount; i++) {
				ForestReplica replica = new ForestReplica();
				replica.setReplicaName(fns.getReplicaName(databaseName, f.getForestName(), i, appConfig));
				replicas.add(replica);

				hostPointer++;
				if (hostPointer == hostNames.size()) {
					hostPointer = 0;
				}

				dataDirectoryPointer++;
				if (dataDirectoryPointer == replicaDataDirectories.size()) {
					dataDirectoryPointer = 0;
				}

				replica.setHost(hostNames.get(hostPointer));

				final String dataDir = replicaDataDirectories.get(dataDirectoryPointer);
				if (dataDir != null && dataDir.trim().length() > 0) {
					replica.setDataDirectory(dataDir);
				}

				configureReplica(replica, databaseName, appConfig);
			}

			f.setForestReplica(replicas);
		}

	}
}
