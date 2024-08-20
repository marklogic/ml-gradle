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
