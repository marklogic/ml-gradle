package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.api.forest.ForestReplica;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.mapper.ResourceMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Based on a given ForestPlan object, builds a list of one or more Forest objects in memory - i.e. nothing is written
 * to MarkLogic, nor does this class make any connections to MarkLogic.
 */
public class ForestBuilder extends LoggingObject {

	private ForestNamingStrategy forestNamingStrategy;

	public ForestBuilder() {
		this(new DefaultForestNamingStrategy());
	}

	public ForestBuilder(ForestNamingStrategy forestNamingStrategy) {
		this.forestNamingStrategy = forestNamingStrategy;
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
		final List<String> hostNames = forestPlan.getHostNames();

		List<String> dataDirectories = determineDataDirectories(databaseName, appConfig);

		int forestsPerDataDirectory = determineForestsPerDataDirectory(forestPlan, appConfig);

		// Number of hosts * number of data directories * number of forests per data directory
		int numberToBuild = hostNames.size() * dataDirectories.size() * forestsPerDataDirectory;

		// So now we have numberToBuild - we want to iterate over each host, and over each data directory
		int forestCounter = (hostNames.size() * dataDirectories.size() * forestPlan.getExistingForestsPerDataDirectory()) + 1;

		forestsPerDataDirectory -= forestPlan.getExistingForestsPerDataDirectory();

		List<Forest> forests = new ArrayList<>();

		for (String hostName : hostNames) {
			for (String dataDirectory : dataDirectories) {
				for (int i = 0; i < forestsPerDataDirectory; i++) {
					if (forestCounter <= numberToBuild) {
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

						forests.add(forest);

						forestCounter++;
					}
				}
			}
		}

		if (forestPlan.getReplicaCount() > 0) {
			addReplicasToForests(forests, forestPlan, appConfig);
		}

		return forests;
	}

	/**
	 * Based on the given ForestPlan - i.e. if replicaCount is greater than zero - replicas will be added to each of
	 * the given Forest objects.
	 *
	 * @param forests
	 * @param forestPlan
	 * @param appConfig
	 */
	public void addReplicasToForests(List<Forest> forests, ForestPlan forestPlan, AppConfig appConfig) {
		final String databaseName = forestPlan.getDatabaseName();
		final List<String> hostNames = forestPlan.getHostNames();
		final int replicaCount = forestPlan.getReplicaCount();

		if (replicaCount >= hostNames.size()) {
			throw new IllegalArgumentException(String.format("Not enough hosts exists to create %d replicas for database '%s'; " +
					"possible hosts, which may include the host with the primary forest and thus cannot have a replica: %s",
				replicaCount, databaseName, hostNames));
		}

		List<String> dataDirectories = determineDataDirectories(forestPlan.getDatabaseName(), appConfig);

		if (appConfig.getReplicaForestDataDirectory() != null) {
			dataDirectories = new ArrayList<>();
			dataDirectories.add(appConfig.getReplicaForestDataDirectory());
		}
		Map<String, String> replicaDataDirectoryMap = appConfig.getDatabaseReplicaDataDirectories();
		if (replicaDataDirectoryMap != null && replicaDataDirectoryMap.containsKey(databaseName)) {
			dataDirectories = new ArrayList<>();
			dataDirectories.add(replicaDataDirectoryMap.get(databaseName));
		}

		for (Forest f : forests) {
			List<ForestReplica> replicas = new ArrayList<>();
			int hostPointer = hostNames.indexOf(f.getHost());
			int dataDirectoryPointer = dataDirectories.indexOf(f.getDataDirectory());
			for (int i = 1; i <= replicaCount; i++) {
				ForestReplica replica = new ForestReplica();
				replica.setReplicaName(getForestReplicaName(databaseName, f.getForestName(), i, appConfig));
				replicas.add(replica);

				hostPointer++;
				if (hostPointer == hostNames.size()) {
					hostPointer = 0;
				}

				dataDirectoryPointer++;
				if (dataDirectoryPointer == dataDirectories.size()) {
					dataDirectoryPointer = 0;
				}

				replica.setHost(hostNames.get(hostPointer));

				final String dataDir = dataDirectories.get(dataDirectoryPointer);
				if (dataDir != null && dataDir.trim().length() > 0) {
					replica.setDataDirectory(dataDir);
				}

				configureReplica(replica, databaseName, appConfig);
			}

			f.setForestReplica(replicas);
		}
	}

	/**
	 * Configures the fast and large data directories for a replica based on what's in AppConfig for the given
	 * database.
	 *
	 * @param replica
	 * @param databaseName
	 * @param appConfig
	 */
	protected void configureReplica(ForestReplica replica, String databaseName, AppConfig appConfig) {
		// First set to the database-agnostic forest directories
		replica.setFastDataDirectory(appConfig.getForestFastDataDirectory());
		replica.setLargeDataDirectory(appConfig.getForestLargeDataDirectory());

		// Now set to the database-specific forest directories if set
		if (databaseName != null) {
			Map<String, String> map = appConfig.getDatabaseFastDataDirectories();
			if (map != null && map.containsKey(databaseName)) {
				replica.setFastDataDirectory(map.get(databaseName));
			}
			map = appConfig.getDatabaseLargeDataDirectories();
			if (map != null && map.containsKey(databaseName)) {
				replica.setLargeDataDirectory(map.get(databaseName));
			}
		}

		// Now set to the replica forest directories if set
		if (appConfig.getReplicaForestFastDataDirectory() != null) {
			replica.setFastDataDirectory(appConfig.getReplicaForestFastDataDirectory());
		}
		if (appConfig.getReplicaForestLargeDataDirectory() != null) {
			replica.setLargeDataDirectory(appConfig.getReplicaForestLargeDataDirectory());
		}

		// And now set to the database-specific replica forest directories if set
		if (databaseName != null) {
			Map<String, String> map = appConfig.getDatabaseReplicaFastDataDirectories();
			if (map != null && map.containsKey(databaseName)) {
				replica.setFastDataDirectory(map.get(databaseName));
			}
			map = appConfig.getDatabaseReplicaLargeDataDirectories();
			if (map != null && map.containsKey(databaseName)) {
				replica.setLargeDataDirectory(map.get(databaseName));
			}
		}
	}


	protected Forest newForest(ForestPlan forestPlan) {
		String template = forestPlan.getTemplate();
		if (template == null) {
			return new Forest();
		}
		try {
			ResourceMapper resourceMapper = new DefaultResourceMapper(new API(null));
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
	 *
	 * @param databaseName
	 * @param forestNumber
	 * @param appConfig
	 * @return
	 */
	protected String getForestName(String databaseName, int forestNumber, AppConfig appConfig) {
		return determineForestNamingStrategy(databaseName, appConfig).getForestName(databaseName, forestNumber, appConfig);
	}

	/**
	 *
	 * @param databaseName
	 * @param forestName
	 * @param forestReplicaNumber
	 * @param appConfig
	 * @return
	 */
	protected String getForestReplicaName(String databaseName, String forestName, int forestReplicaNumber, AppConfig appConfig) {
		return determineForestNamingStrategy(databaseName, appConfig).getReplicaName(databaseName, forestName, forestReplicaNumber, appConfig);
	}

	/**
	 *
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
}
