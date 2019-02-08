package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.mgmt.api.forest.Forest;

import java.util.List;

public interface ReplicaBuilderStrategy {

	/**
	 * Defines how replica forests should be constructed for a particular database.
	 *
	 * @param forests                the list of forests that have already been determined for the database
	 * @param forestPlan             captures inputs for what forests and replicas should be built
	 * @param appConfig
	 * @param replicaDataDirectories the list of data directories to use for the replica forests
	 * @param forestNamingStrategy   a strategy for how the replica forests are named
	 */
	void buildReplicas(List<Forest> forests, ForestPlan forestPlan, AppConfig appConfig,
	                   List<String> replicaDataDirectories, ForestNamingStrategy forestNamingStrategy);

}
