package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractUndoableCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.api.forest.ForestReplica;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.resource.forests.ForestManager;
import com.marklogic.mgmt.resource.forests.ForestStatus;
import com.marklogic.mgmt.resource.hosts.HostManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command for configuring - i.e. creating and setting - replica forests for existing databases and/or primary forests.
 * It's normally easier to just specify the databases that you want to configure forest replicas for, but this command
 * does provide the ability to configure replicas for specific forests.
 * <p>
 * Very useful for the out-of-the-box forests such as Security, Schemas, App-Services, and Meters, which normally need
 * replicas for failover in a cluster.
 */
public class ConfigureForestReplicasCommand extends AbstractUndoableCommand {

	private Map<String, Integer> databaseNamesAndReplicaCounts = new HashMap<>();
	private Map<String, Integer> forestNamesAndReplicaCounts = new HashMap<>();
	private boolean deleteReplicasOnUndo = true;

	/**
	 * By default, the execute sort order is Integer.MAX_VALUE as a way of guaranteeing that the referenced primary
	 * forests already exist. Feel free to customize as needed.
	 */
	public ConfigureForestReplicasCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_FOREST_REPLICAS);
		setUndoSortOrder(SortOrderConstants.DELETE_FOREST_REPLICAS);
	}

	/**
	 * Allows for the map of database names and counts to be configured as a comma-delimited string of the form:
	 * "dbName,replicaCount,dbName,replicaCount,etc".
	 *
	 * @param str
	 */
	public void setDatabaseNamesAndReplicaCountsAsString(String str) {
		databaseNamesAndReplicaCounts.clear();
		String[] tokens = str.split(",");
		for (int i = 0; i < tokens.length; i += 2) {
			String dbName = tokens[i];
			int count = Integer.parseInt(tokens[i + 1]);
			databaseNamesAndReplicaCounts.put(dbName, count);
		}
	}

	@Override
	public void execute(CommandContext context) {
		String str = context.getAppConfig().getDatabaseNamesAndReplicaCounts();
		if (str != null) {
			setDatabaseNamesAndReplicaCountsAsString(str);
		}

		if ((databaseNamesAndReplicaCounts == null || databaseNamesAndReplicaCounts.isEmpty())
			&& (forestNamesAndReplicaCounts == null || forestNamesAndReplicaCounts.isEmpty())) {
			return;
		}
		HostManager hostMgr = new HostManager(context.getManageClient());
		ForestManager forestMgr = new ForestManager(context.getManageClient());

		List<String> hostIds = hostMgr.getHostIds();
		if (hostIds.size() == 1) {
			if (logger.isInfoEnabled()) {
				logger.info("Only found one host ID, so not configuring any replica forests; host ID: "
					+ hostIds.get(0));
			}
			return;
		}

		for (String databaseName : databaseNamesAndReplicaCounts.keySet()) {
			int replicaCount = databaseNamesAndReplicaCounts.get(databaseName);
			if (replicaCount > 0) {
				configureDatabaseReplicaForests(databaseName, replicaCount, hostIds, context);
			}
		}

		for (String forestName : forestNamesAndReplicaCounts.keySet()) {
			int replicaCount = forestNamesAndReplicaCounts.get(forestName);
			if (replicaCount > 0) {
				configureReplicaForests(null, forestName, replicaCount, hostIds, context, forestMgr);
			}
		}
	}

	@Override
	public void undo(CommandContext context) {
		if (deleteReplicasOnUndo) {
			String str = context.getAppConfig().getDatabaseNamesAndReplicaCounts();
			if (str != null) {
				setDatabaseNamesAndReplicaCountsAsString(str);
			}

			DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());
			ForestManager forestMgr = new ForestManager(context.getManageClient());

			for (String databaseName : databaseNamesAndReplicaCounts.keySet()) {
				logger.info(format("Deleting forest replicas for database %s", databaseName));
				if (!dbMgr.exists(databaseName)) {
					logger.warn(format("Database %s does not exist, so not able to delete forest replica for it; perhaps a previous command deleted the database?", databaseName));
				}
				else {
					List<String> forestNames = dbMgr.getForestNames(databaseName);
					for (String forestName : forestNames) {
						deleteReplicas(forestName, forestMgr);
					}
					logger.info(format("Finished deleting forest replicas for database %s", databaseName));
				}
			}

			for (String forestName : forestNamesAndReplicaCounts.keySet()) {
				deleteReplicas(forestName, forestMgr);
			}
		} else {
			logger.info("deleteReplicasOnUndo is set to false, so not deleting any replicas");
		}
	}

	protected void deleteReplicas(String forestName, ForestManager forestMgr) {
		if (forestMgr.exists(forestName)) {
			ForestStatus status = forestMgr.getForestStatus(forestName);
			if (status.isPrimary() && status.hasReplicas()) {
				logger.info(format("Deleting forest replicas for primary forest %s", forestName));
				forestMgr.deleteReplicas(forestName);
				logger.info(format("Finished deleting forest replicas for primary forest %s", forestName));
			}
		}
	}

	/**
	 * For the given database, find all of its primary forests. Then for each primary forest, just call
	 * configureReplicaForests. And that should be smart enough to say - if the primary forest already has replicas,
	 * then don't do anything.
	 *
	 * @param databaseName
	 * @param replicaCount
	 * @param hostIds
	 */
	protected void configureDatabaseReplicaForests(String databaseName, int replicaCount, List<String> hostIds,
	                                               CommandContext context) {
		ForestManager forestMgr = new ForestManager(context.getManageClient());
		DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());
		List<String> forestNames = dbMgr.getForestNames(databaseName);
		for (String name : forestNames) {
			configureReplicaForests(databaseName, name, replicaCount, hostIds, context, forestMgr);
		}
	}

	/**
	 * Creates forests as needed (they may already exists) and then sets those forests as the replicas for the given
	 * primaryForestName.
	 *
	 * @param databaseName
	 * @param forestIdOrName
	 * @param replicaCount
	 * @param hostIds
	 * @param context
	 * @param forestMgr
	 */
	protected void configureReplicaForests(String databaseName, String forestIdOrName, int replicaCount, List<String> hostIds,
	                                       CommandContext context, ForestManager forestMgr) {
		ForestStatus status = forestMgr.getForestStatus(forestIdOrName);
		if (!status.isPrimary()) {
			logger.info(format("Forest %s is not a primary forest, so not configuring replica forests", forestIdOrName));
			return;
		}
		if (status.hasReplicas()) {
			logger.info(format("Forest %s already has replicas, so not configuring replica forests", forestIdOrName));
			return;
		}

		logger.info(format("Creating forest replicas for primary forest %s", forestIdOrName));
		createReplicaForests(databaseName, forestIdOrName, replicaCount, hostIds, context, forestMgr);
		logger.info(format("Finished creating forest replicas for primary forest %s", forestIdOrName));
	}

	/**
	 * Finds the host that the forest is on, and then starting with the next host in the list of host IDs,
	 * creates N replicas.
	 *
	 * @param databaseName
	 * @param forestIdOrName
	 * @param replicaCount
	 * @param hostIds
	 * @param context
	 * @param forestMgr
	 * @return a map where the keys are replica forest names, and the value of each key is the ID of the host that
	 * the replica was created on
	 */
	protected Map<String, String> createReplicaForests(String databaseName, String forestIdOrName, int replicaCount, List<String> hostIds,
	                                                   CommandContext context, ForestManager forestMgr) {

		// Using the Forest class to generate JSON
		API api = new API(context.getManageClient());
		Forest forest = new Forest(api, null);
		List<ForestReplica> replicas = new ArrayList<>();
		forest.setForestReplica(replicas);

		String primaryForestHostId = forestMgr.getHostId(forestIdOrName);
		Map<String, String> replicaNamesAndHostIds = new HashMap<>();
		int size = hostIds.size();
		for (int i = 0; i < size; i++) {
			String hostId = hostIds.get(i);
			if (hostId.equals(primaryForestHostId)) {
				int nextReplicaHostIndex = i + 1;
				for (int j = 1; j <= replicaCount; j++) {
					if (nextReplicaHostIndex >= size) {
						nextReplicaHostIndex = 0;
					}
					String replicaHostId = hostIds.get(nextReplicaHostIndex);
					String name = forestIdOrName + "-replica-" + j;
					replicas.add(buildForestReplica(databaseName, name, replicaHostId, context.getAppConfig()));
					replicaNamesAndHostIds.put(name, replicaHostId);
					nextReplicaHostIndex++;
				}
			}
		}

		String json = forest.getJson();
		context.getManageClient().putJson(forestMgr.getPropertiesPath(forestIdOrName), json);
		return replicaNamesAndHostIds;
	}

	/**
	 * Return a ForestReplica instance with its properties configured based on what's in AppConfig.
	 *
	 * @param databaseName
	 * @param name
	 * @param replicaHostId
	 * @param appConfig
	 * @return
	 */
	protected ForestReplica buildForestReplica(String databaseName, String name, String replicaHostId, AppConfig appConfig) {
		ForestReplica replica = new ForestReplica();
		replica.setHost(replicaHostId);
		replica.setReplicaName(name);

		// First set to the database-agnostic forest directories
		replica.setDataDirectory(appConfig.getForestDataDirectory());
		replica.setFastDataDirectory(appConfig.getForestFastDataDirectory());
		replica.setLargeDataDirectory(appConfig.getForestLargeDataDirectory());

		// Now set to the database-specific forest directories if set
		if (databaseName != null) {
			Map<String, String> map = appConfig.getDatabaseDataDirectories();
			if (map != null && map.containsKey(databaseName)) {
				replica.setDataDirectory(map.get(databaseName));
			}

			map = appConfig.getDatabaseFastDataDirectories();
			if (map != null && map.containsKey(databaseName)) {
				replica.setFastDataDirectory(map.get(databaseName));
			}

			map = appConfig.getDatabaseLargeDataDirectories();
			if (map != null && map.containsKey(databaseName)) {
				replica.setLargeDataDirectory(map.get(databaseName));
			}
		}

		// Now set to the replica forest directories if set
		if (appConfig.getReplicaForestDataDirectory() != null) {
			replica.setDataDirectory(appConfig.getReplicaForestDataDirectory());
		}
		if (appConfig.getReplicaForestFastDataDirectory() != null) {
			replica.setFastDataDirectory(appConfig.getReplicaForestFastDataDirectory());
		}
		if (appConfig.getReplicaForestLargeDataDirectory() != null) {
			replica.setLargeDataDirectory(appConfig.getReplicaForestLargeDataDirectory());
		}

		// And now set to the database-specific replica forest directories if set
		if (databaseName != null) {
			Map<String, String> map = appConfig.getDatabaseReplicaDataDirectories();
			if (map != null && map.containsKey(databaseName)) {
				replica.setDataDirectory(map.get(databaseName));
			}

			map = appConfig.getDatabaseReplicaFastDataDirectories();
			if (map != null && map.containsKey(databaseName)) {
				replica.setFastDataDirectory(map.get(databaseName));
			}

			map = appConfig.getDatabaseReplicaLargeDataDirectories();
			if (map != null && map.containsKey(databaseName)) {
				replica.setLargeDataDirectory(map.get(databaseName));
			}
		}

		return replica;
	}

	public Map<String, Integer> getForestNamesAndReplicaCounts() {
		return forestNamesAndReplicaCounts;
	}

	public void setForestNamesAndReplicaCounts(Map<String, Integer> forestNamesAndReplicaCounts) {
		this.forestNamesAndReplicaCounts = forestNamesAndReplicaCounts;
	}

	public void setDeleteReplicasOnUndo(boolean deleteReplicasOnUndo) {
		this.deleteReplicasOnUndo = deleteReplicasOnUndo;
	}

	public Map<String, Integer> getDatabaseNamesAndReplicaCounts() {
		return databaseNamesAndReplicaCounts;
	}

	public void setDatabaseNamesAndReplicaCounts(Map<String, Integer> databaseNamesAndReplicaCounts) {
		this.databaseNamesAndReplicaCounts = databaseNamesAndReplicaCounts;
	}
}
