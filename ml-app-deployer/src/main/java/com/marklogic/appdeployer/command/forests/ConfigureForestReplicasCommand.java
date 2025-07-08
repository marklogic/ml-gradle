/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractUndoableCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.configuration.Configuration;
import com.marklogic.mgmt.api.configuration.Configurations;
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.mapper.ResourceMapper;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.resource.forests.ForestManager;
import com.marklogic.mgmt.resource.forests.ForestStatus;
import com.marklogic.mgmt.resource.groups.GroupManager;
import com.marklogic.mgmt.resource.hosts.HostManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Command for configuring - i.e. creating and setting - replica forests for existing databases. The expectation is that
 * {@code DeployForestsCommand} is used for creating primary forests, while this command is used for creating replica
 * forests based on existing primary forests.
 */
public class ConfigureForestReplicasCommand extends AbstractUndoableCommand {

	private boolean deleteReplicasOnUndo = true;
	private GroupHostNamesProvider groupHostNamesProvider;
	private ForestBuilder forestBuilder = new ForestBuilder();

	public ConfigureForestReplicasCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_FOREST_REPLICAS);
		setUndoSortOrder(SortOrderConstants.DELETE_FOREST_REPLICAS);
	}

	@Override
	public void execute(CommandContext context) {
		Map<String, Integer> databaseNamesAndReplicaCounts = context.getAppConfig().getDatabaseNamesAndReplicaCounts();
		if (databaseNamesAndReplicaCounts == null || databaseNamesAndReplicaCounts.isEmpty()) {
			logger.info("No database names and replica counts defined, so not configuring any forest replicas.");
			return;
		}

		List<String> allHostNames = new HostManager(context.getManageClient()).getHostNames();
		if (allHostNames.size() < 2) {
			if (logger.isInfoEnabled()) {
				logger.info("Only found one host, so not configuring any replica forests; host: {}", allHostNames.get(0));
			}
			return;
		}

		for (String databaseName : databaseNamesAndReplicaCounts.keySet()) {
			if (databaseNamesAndReplicaCounts.get(databaseName) > 0) {
				configureDatabaseReplicaForests(databaseName, allHostNames, context);
			}
		}
	}

	@Override
	public void undo(CommandContext context) {
		if (deleteReplicasOnUndo && context.getAppConfig().getDatabaseNamesAndReplicaCounts() != null) {
			Map<String, Integer> databaseNamesAndReplicaCounts = context.getAppConfig().getDatabaseNamesAndReplicaCounts();
			DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());
			ForestManager forestMgr = new ForestManager(context.getManageClient());

			for (String databaseName : databaseNamesAndReplicaCounts.keySet()) {
				logger.info(format("Deleting forest replicas for database %s", databaseName));
				if (!dbMgr.exists(databaseName)) {
					logger.warn(format("Database %s does not exist, so not able to delete forest replica for it; perhaps a previous command deleted the database?", databaseName));
				} else {
					List<String> forestNames = dbMgr.getForestNames(databaseName);
					for (String forestName : forestNames) {
						deleteReplicas(forestName, forestMgr);
					}
					logger.info(format("Finished deleting forest replicas for database %s", databaseName));
				}
			}
		} else {
			logger.info("Not deleting any replicas.");
		}
	}

	/**
	 * @param forestName
	 * @param forestMgr
	 */
	private void deleteReplicas(String forestName, ForestManager forestMgr) {
		if (forestMgr.exists(forestName)) {
			ForestStatus status = forestMgr.getForestStatus(forestName);
			if (status.isPrimary() && status.hasReplicas()) {
				logger.info(format("Deleting forest replicas for primary forest %s", forestName));
				forestMgr.deleteReplicas(forestName);
				logger.info(format("Finished deleting forest replicas for primary forest %s", forestName));
			}
		}
	}

	private void configureDatabaseReplicaForests(String databaseName, List<String> allHostNames, CommandContext context) {
		List<Forest> forestsNeedingReplicas = determineForestsNeedingReplicas(databaseName, context);
		addReplicasToForests(databaseName, forestsNeedingReplicas, allHostNames, context);
		createForestReplicas(forestsNeedingReplicas, context.getAppConfig(), context.getManageClient());
	}

	/**
	 * Allows for ForestPlanner to preview the list of forest replicas to create. Ultimately, all this code should move
	 * out of the command hierarchy so it's much more easily reused.
	 */
	public void addReplicasToForests(String databaseName, List<Forest> forests, List<String> allHostNames, CommandContext context) {
		int replicaCount = 0;
		if (context.getAppConfig().getDatabaseNamesAndReplicaCounts() != null &&
			context.getAppConfig().getDatabaseNamesAndReplicaCounts().containsKey(databaseName)) {
			replicaCount = context.getAppConfig().getDatabaseNamesAndReplicaCounts().get(databaseName);
		}

		if (replicaCount < 1) {
			logger.info("No replicas for: {}", databaseName);
			return;
		}

		List<String> selectedHostNames = getHostNamesForDatabaseForests(databaseName, allHostNames, context);
		List<String> dataDirectories = forestBuilder.determineDataDirectories(databaseName, context.getAppConfig());
		Map<String, String> hostNamesAndZones = getHostNamesAndZones(context.getManageClient());

		final ForestPlan forestPlan = new ForestPlan(databaseName, selectedHostNames)
			.withHostsToZones(hostNamesAndZones)
			.withReplicaCount(replicaCount);

		forestBuilder.addReplicasToForests(forests, forestPlan, context.getAppConfig(), dataDirectories);
	}

	/**
	 * @return a map of host names to optional zones. For performance reasons, as soon as a host is found to not have
	 * a zone, then an empty map will be returned, as zones will not matter in that scenario.
	 */
	private Map<String, String> getHostNamesAndZones(ManageClient manageClient) {
		HostManager hostManager = new HostManager(manageClient);
		PayloadParser payloadParser = new PayloadParser();
		Map<String, String> map = new LinkedHashMap<>();
		for (String hostName : hostManager.getHostNames()) {
			String json = manageClient.getJson("/manage/v2/hosts/%s/properties".formatted(hostName));
			String zone = payloadParser.getPayloadFieldValue(json, "zone");
			if (zone == null || zone.trim().isEmpty()) {
				return map;
			}
			map.put(hostName, zone);
		}
		return map;
	}

	public List<Forest> removeForestDetails(List<Forest> forests) {
		return forests.stream().map(forest -> {
			Forest forestWithOnlyReplicas = new Forest();
			forestWithOnlyReplicas.setForestName(forest.getForestName());
			forestWithOnlyReplicas.setHost(forest.getHost());
			forestWithOnlyReplicas.setForestReplica(forest.getForestReplica());
			return forestWithOnlyReplicas;
		}).toList();
	}

	private void createForestReplicas(List<Forest> forestsNeedingReplicas, AppConfig appConfig, ManageClient manageClient) {
		// Trim off all forests details so only the replicas are saved.
		List<Forest> forestsWithOnlyReplicas = removeForestDetails(forestsNeedingReplicas);

		// As of 4.5.3, try CMA first so that this can be done in a single request instead of one request per forest.
		if (appConfig.getCmaConfig().isDeployForests()) {
			try {
				saveReplicasViaCma(forestsWithOnlyReplicas, manageClient);
				return;
			} catch (Exception ex) {
				logger.warn("Unable to create forest replicas via CMA; cause: {}; will fall back to using /manage/v2.", ex.getMessage());
			}
		}

		// If we get here, either CMA usage is disabled or an error occurred with CMA.
		saveReplicasOneByOne(forestsWithOnlyReplicas, manageClient);
	}

	private void saveReplicasViaCma(List<Forest> forestsWithOnlyReplicas, ManageClient manageClient) {
		Configuration config = new Configuration();
		forestsWithOnlyReplicas.forEach(forest -> config.addForest(forest.toObjectNode()));
		new Configurations(config).submit(manageClient);
	}

	private void saveReplicasOneByOne(List<Forest> forestsWithOnlyReplicas, ManageClient manageClient) {
		ForestManager forestManager = new ForestManager(manageClient);
		forestsWithOnlyReplicas.forEach(forest -> {
			String forestName = forest.getForestName();
			logger.info(format("Creating forest replicas for primary forest %s", forestName));
			manageClient.putJson(forestManager.getPropertiesPath(forestName), forest.getJson());
			logger.info(format("Finished creating forest replicas for primary forest %s", forestName));
		});
	}

	/**
	 * Per #389, the list of replicas needs to be calculated for all forests at once so that ForestBuilder produces the
	 * correct results.
	 * <p>
	 * For 6.0.0, made this public so it can be reused by ForestPlanner. This should be much cleaner once all this logic
	 * is moved out of the command hierarchy and into a more reusable class.
	 *
	 * @param databaseName
	 * @param context
	 * @return
	 */
	public List<Forest> determineForestsNeedingReplicas(String databaseName, CommandContext context) {
		ForestManager forestManager = new ForestManager(context.getManageClient());
		DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());
		API api = new API(context.getManageClient());
		ResourceMapper resourceMapper = new DefaultResourceMapper(api);

		List<Forest> forestsNeedingReplicas = new ArrayList<>();

		Map<String, List<Forest>> mapOfPrimaryForests = context.getMapOfPrimaryForests();

		/**
		 * In both blocks below, a forest is not included if it already has replicas. This logic dates back to 2015,
		 * and is likely due to uncertainty over the various scenarios that can occur if a forest does already have
		 * replicas. At least as of August 2023, MarkLogic recommends a single replica per forest. Given that no users
		 * have asked for this check to not be performed and based on MarkLogic's recommendation, it seems reasonable
		 * to leave this check in for now. However, some ad hoc testing has indicated that this check is unnecessary
		 * and that it appears to safe to vary the number of replicas per forest. So it likely would be beneficial to
		 * remove this check at some point.
		 */
		if (mapOfPrimaryForests != null && mapOfPrimaryForests.containsKey(databaseName)) {
			mapOfPrimaryForests.get(databaseName).forEach(forest -> {
				boolean forestHasReplicasAlready = forest.getForestReplica() != null && !forest.getForestReplica().isEmpty();
				if (!forestHasReplicasAlready) {
					forestsNeedingReplicas.add(forest);
				} else {
					logger.info("Forest {} already has replicas, so not configuring replica forests.", forest.getForestName());
				}
			});
		} else {
			for (String forestName : dbMgr.getForestNames(databaseName)) {
				logger.info("Checking the status of forest {} to determine if it is a primary forest and whether or not it has replicas already.", forestName);
				ForestStatus status = forestManager.getForestStatus(forestName);
				if (!status.isPrimary()) {
					logger.info("Forest {} is not a primary forest, so not configuring replica forests.", forestName);
					continue;
				}
				if (status.hasReplicas()) {
					logger.info("Forest {} already has replicas, so not configuring replica forests.", forestName);
					continue;
				}

				String forestJson = forestManager.getPropertiesAsJson(forestName);
				Forest forest = resourceMapper.readResource(forestJson, Forest.class);
				forestsNeedingReplicas.add(forest);
			}
		}

		return forestsNeedingReplicas;
	}

	protected List<String> getHostNamesForDatabaseForests(String databaseName, List<String> allHostNames, CommandContext context) {
		List<String> selectedHostNames = new ArrayList<>();

		Map<String, List<String>> databaseGroupMap = context.getAppConfig().getDatabaseGroups();
		List<String> databaseGroups = databaseGroupMap != null ? databaseGroupMap.get(databaseName) : null;

		Map<String, List<String>> databaseHostMap = context.getAppConfig().getDatabaseHosts();
		List<String> databaseHosts = databaseHostMap != null ? databaseHostMap.get(databaseName) : null;

		if (databaseGroups != null && !databaseGroups.isEmpty()) {
			if (groupHostNamesProvider == null) {
				groupHostNamesProvider = groupName -> new GroupManager(context.getManageClient()).getHostNames(groupName);
			}

			if (logger.isInfoEnabled()) {
				logger.info(format("Creating replica forests on hosts in groups %s for database '%s'", databaseGroups, databaseName));
			}

			for (String groupName : databaseGroups) {
				List<String> groupHostNames = groupHostNamesProvider.getGroupHostNames(groupName);
				if (groupHostNames == null || groupHostNames.isEmpty()) {
					logger.warn("No hosts found for group: {}", groupName);
					continue;
				}

				for (String hostName : allHostNames) {
					if (groupHostNames.contains(hostName)) {
						selectedHostNames.add(hostName);
					}
				}
			}

			if (!selectedHostNames.isEmpty()) {
				if (logger.isInfoEnabled()) {
					logger.info(format("Creating forests on hosts %s based on groups %s for database '%s'", selectedHostNames, databaseGroups, databaseName));
				}
				if (databaseHosts != null && !databaseHosts.isEmpty()) {
					logger.warn(format("Database groups and database hosts were both specified for database '%s'; " +
						"only database groups are being used, database hosts will be ignored.", databaseName));
				}
				return selectedHostNames;
			}

			logger.warn("Did not find any valid hosts in selected groups: {}", databaseGroups);
		}

		/**
		 * If no database groups were specified, then retain any host that is either in the set of database hosts, or
		 * all hosts in case no database hosts were specified.
		 */
		for (String hostName : allHostNames) {
			if ((databaseHosts == null || databaseHosts.contains(hostName))) {
				selectedHostNames.add(hostName);
			}
		}

		return selectedHostNames;
	}

	public void setDeleteReplicasOnUndo(boolean deleteReplicasOnUndo) {
		this.deleteReplicasOnUndo = deleteReplicasOnUndo;
	}

	public void setGroupHostNamesProvider(GroupHostNamesProvider groupHostNamesProvider) {
		this.groupHostNamesProvider = groupHostNamesProvider;
	}
}

/**
 * This really only exists to facilitate unit testing.
 */
interface GroupHostNamesProvider {
	List<String> getGroupHostNames(String groupName);
}
