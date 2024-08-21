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
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.mapper.ResourceMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Based on a given ForestPlan object, builds a list of one or more Forest objects in memory - i.e. nothing is written
 * to MarkLogic, nor does this class make any connections to MarkLogic.
 */
public class ForestBuilder extends LoggingObject {

	private ForestNamingStrategy forestNamingStrategy;
	private ReplicaBuilderStrategy replicaBuilderStrategy;
	private ResourceMapper resourceMapper;

	public ForestBuilder() {
		this(new DefaultForestNamingStrategy());
	}

	public ForestBuilder(ForestNamingStrategy forestNamingStrategy) {
		this.forestNamingStrategy = forestNamingStrategy;
		this.replicaBuilderStrategy = new DistributedReplicaBuilderStrategy();
		this.resourceMapper = new DefaultResourceMapper(new API(null));
	}

	/**
	 * Builds a list of Forest objects based on the given ForestPlan and AppConfig. If replicaCount on the ForestPlan
	 * is greater than zero, then ForestReplica objects are added to each Forest as well.
	 * <p>
	 * Note that the number of forests per data directory in the ForestPlan is overridden by the getForestCounts
	 * map in the AppConfig object, if the map has an entry for the database name in the ForestPlan.
	 *
	 * @param forestPlan
	 * @param appConfig
	 * @return
	 */
	public List<Forest> buildForests(ForestPlan forestPlan, AppConfig appConfig) {
		final String databaseName = forestPlan.getDatabaseName();

		// Find out what forests we have already, keyed on host and then data directory
		Map<String, Map<String, List<Forest>>> existingForestsMap = existingForestsMap(forestPlan);

		final int forestsPerDataDirectory = determineForestsPerDataDirectory(forestPlan, appConfig);
		final List<String> dataDirectories = determineDataDirectories(databaseName, appConfig);

		List<Forest> forestsToBuild = new ArrayList<>();

		// For naming any new forests we build, start with the current count and bump up as we build each forest
		int forestCounter = forestPlan.getExistingForests().size();

		/**
		 * Now loop over each host, and for each host, loop over each data directory. See how many forests exist already
		 * in that data directory. Build new forests as needed so each data directory has the correct amount.
		 */
		for (String hostName : forestPlan.getHostNames()) {
			Map<String, List<Forest>> hostMap = existingForestsMap.get(hostName);
			for (String dataDirectory : dataDirectories) {
				int forestsToCreate = forestsPerDataDirectory;
				if (hostMap != null && hostMap.containsKey(dataDirectory)) {
					forestsToCreate -= hostMap.get(dataDirectory).size();
				}
				for (int i = 0; i < forestsToCreate; i++) {
					forestCounter++;
					Forest forest = newForest(forestPlan);
					forest.setForestName(getForestName(databaseName, forestCounter, appConfig));
					forest.setHost(hostName);
					forest.setDatabase(databaseName);

					if (dataDirectory != null && dataDirectory.trim().length() > 0) {
						forest.setDataDirectory(dataDirectory);
					}

					// First see if we have any database-agnostic forest directories
					if (appConfig.getForestFastDataDirectory() != null) {
						forest.setFastDataDirectory(appConfig.getForestFastDataDirectory());
					}
					if (appConfig.getForestLargeDataDirectory() != null) {
						forest.setLargeDataDirectory(appConfig.getForestLargeDataDirectory());
					}

					// Now check for database-specific forest directories
					Map<String, String> map = appConfig.getDatabaseFastDataDirectories();
					if (map != null && map.containsKey(databaseName)) {
						forest.setFastDataDirectory(map.get(databaseName));
					}
					map = appConfig.getDatabaseLargeDataDirectories();
					if (map != null && map.containsKey(databaseName)) {
						forest.setLargeDataDirectory(map.get(databaseName));
					}

					forestsToBuild.add(forest);
				}
			}
		}

		if (forestPlan.getReplicaCount() > 0) {
			addReplicasToForests(forestsToBuild, forestPlan, appConfig, dataDirectories);
		}

		return forestsToBuild;
	}

	/**
	 * Returns a map with keys of host names, where each value is a map whose keys are data directory paths bound to
	 * a list of forests that already exist at each data directory path.
	 *
	 * @param forestPlan
	 * @return
	 */
	protected Map<String, Map<String, List<Forest>>> existingForestsMap(ForestPlan forestPlan) {
		Map<String, Map<String, List<Forest>>> existingForestsMap = new LinkedHashMap<>();
		for (Forest f : forestPlan.getExistingForests()) {
			String host = f.getHost();
			String dataDirectory = f.getDataDirectory();
			if (dataDirectory == null) {
				dataDirectory = "";
			}

			Map<String, List<Forest>> dataDirectoryMap = existingForestsMap.computeIfAbsent(host, k -> new LinkedHashMap<>());

			List<Forest> list = dataDirectoryMap.computeIfAbsent(dataDirectory, k -> new ArrayList<>());
			list.add(f);
		}
		return existingForestsMap;
	}

	/**
	 * Based on the given ForestPlan - i.e. if replicaCount is greater than zero - replicas will be added to each of
	 * the given Forest objects.
	 *
	 * @param forests
	 * @param forestPlan
	 * @param appConfig
	 */
	public void addReplicasToForests(List<Forest> forests, ForestPlan forestPlan, AppConfig appConfig, List<String> dataDirectories) {
		final String databaseName = forestPlan.getDatabaseName();
		final List<String> hostNames = forestPlan.getReplicaHostNames();
		final int replicaCount = forestPlan.getReplicaCount();

		if (replicaCount >= hostNames.size()) {
			throw new IllegalArgumentException(String.format("Not enough hosts exists to create %d replicas for database '%s'; " +
					"possible hosts, which may include the host with the primary forest and thus cannot have a replica: %s",
				replicaCount, databaseName, hostNames));
		}

		// Determine if there are replica-specific data directories. If not, use the primary ones.
		List<String> replicaDataDirectories = determineReplicaDataDirectories(forestPlan, appConfig);
		if (replicaDataDirectories == null) {
			replicaDataDirectories = dataDirectories;
		}

		ReplicaBuilderStrategy strategyToUse = replicaBuilderStrategy;
		if (appConfig.getReplicaBuilderStrategy() != null) {
			if (logger.isInfoEnabled()) {
				logger.info("Using ReplicaBuilderStrategy defined in AppConfig");
			}
			strategyToUse = appConfig.getReplicaBuilderStrategy();
		}

		strategyToUse.buildReplicas(forests, forestPlan, appConfig, replicaDataDirectories, determineForestNamingStrategy(databaseName, appConfig));
	}

	protected Forest newForest(ForestPlan forestPlan) {
		String template = forestPlan.getTemplate();
		if (template == null) {
			return new Forest();
		}
		try {
			return resourceMapper.readResource(template, Forest.class);
		} catch (Exception ex) {
			logger.warn("Unable to construct a new Forest using template: " + template, ex);
		}
		return new Forest();
	}

	/**
	 * Based on what's in AppConfig for the given database, constructs a list of one or more directories.
	 *
	 * @param databaseName
	 * @param appConfig
	 * @return
	 */
	protected List<String> determineDataDirectories(String databaseName, AppConfig appConfig) {
		List<String> dataDirectories = null;
		if (appConfig.getDatabaseDataDirectories() != null) {
			dataDirectories = appConfig.getDatabaseDataDirectories().get(databaseName);
		}

		if (dataDirectories == null || dataDirectories.isEmpty()) {
			dataDirectories = new ArrayList<>();

			// Check for a database-agnostic data directory
			if (appConfig.getForestDataDirectory() != null) {
				dataDirectories.add(appConfig.getForestDataDirectory());
			} else {
				// Placeholder to ensure we have at least one data directory
				dataDirectories.add("");
			}
		}

		return dataDirectories;
	}

	/**
	 * Determines if there are replica-specific data directories for the database associated with the ForestPlan. If
	 * not, then null will be returned.
	 *
	 * @param forestPlan
	 * @param appConfig
	 * @return
	 */
	protected List<String> determineReplicaDataDirectories(ForestPlan forestPlan, AppConfig appConfig) {
		List<String> replicaDataDirectories = null;
		if (appConfig.getReplicaForestDataDirectory() != null) {
			replicaDataDirectories = new ArrayList<>();
			replicaDataDirectories.add(appConfig.getReplicaForestDataDirectory());
		}

		Map<String, List<String>> replicaDataDirectoryMap = appConfig.getDatabaseReplicaDataDirectories();
		final String databaseName = forestPlan.getDatabaseName();
		if (replicaDataDirectoryMap != null && replicaDataDirectoryMap.containsKey(databaseName)) {
			replicaDataDirectories = new ArrayList<>(replicaDataDirectoryMap.get(databaseName));
		}

		return replicaDataDirectories;
	}

	/**
	 * TODO This a little wonky in that it's using appConfig.getForestCounts, which is currently understood to be the
	 * number of forests per host, not per data directory. Probably need a new property in appConfig to make this more
	 * clear.
	 *
	 * @param forestPlan
	 * @param appConfig
	 * @return
	 */
	protected int determineForestsPerDataDirectory(ForestPlan forestPlan, AppConfig appConfig) {
		int forestCount = forestPlan.getForestsPerDataDirectory();
		Map<String, Integer> forestCounts = appConfig.getForestCounts();
		if (forestCounts != null && forestCounts.containsKey(forestPlan.getDatabaseName())) {
			Integer i = forestCounts.get(forestPlan.getDatabaseName());
			if (i != null) {
				forestCount = i;
			}
		}
		return forestCount;
	}

	/**
	 * @param databaseName
	 * @param forestNumber
	 * @param appConfig
	 * @return
	 */
	protected String getForestName(String databaseName, int forestNumber, AppConfig appConfig) {
		return determineForestNamingStrategy(databaseName, appConfig).getForestName(databaseName, forestNumber, appConfig);
	}

	/**
	 * @param databaseName
	 * @param appConfig
	 * @return
	 */
	protected ForestNamingStrategy determineForestNamingStrategy(String databaseName, AppConfig appConfig) {
		ForestNamingStrategy fns = null;
		Map<String, ForestNamingStrategy> map = appConfig.getForestNamingStrategies();
		if (map != null) {
			fns = map.get(databaseName);
		}
		return fns != null ? fns : this.forestNamingStrategy;
	}

	public void setReplicaBuilderStrategy(ReplicaBuilderStrategy replicaBuilderStrategy) {
		this.replicaBuilderStrategy = replicaBuilderStrategy;
	}

	public ReplicaBuilderStrategy getReplicaBuilderStrategy() {
		return replicaBuilderStrategy;
	}

	public ForestNamingStrategy getForestNamingStrategy() {
		return forestNamingStrategy;
	}
}
