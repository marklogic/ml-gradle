package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.resource.hosts.HostNameProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultHostCalculator extends LoggingObject implements HostCalculator {

	private HostNameProvider hostNameProvider;

	public DefaultHostCalculator(HostNameProvider hostNameProvider) {
		this.hostNameProvider = hostNameProvider;
	}

	@Override
	public List<String> calculateHostNames(String databaseName, CommandContext context) {
		List<String> hostNamesFromDatabaseGroups = determineHostsNamesBasedOnDatabaseGroups(databaseName, context);
		if (hostNamesFromDatabaseGroups != null) {
			if (hostNamesFromDatabaseGroups.size() > 1 && context.getAppConfig().isDatabaseWithForestsOnOneHost(databaseName)) {
				return hostNamesFromDatabaseGroups.subList(0, 1);
			}
			return hostNamesFromDatabaseGroups;
		}

		if (logger.isInfoEnabled()) {
			logger.info("Finding eligible hosts for forests for database: " + databaseName);
		}

		List<String> hostNames = hostNameProvider.getHostNames();

		List<String> hostNamesFromDatabaseHosts = determineHostNamesBasedOnDatabaseHosts(databaseName, context, hostNames);
		if (hostNamesFromDatabaseHosts != null) {
			return hostNamesFromDatabaseHosts;
		}

		return hostNames;
	}

	/**
	 * @param context
	 * @return
	 */
	protected List<String> determineHostsNamesBasedOnDatabaseGroups(String databaseName, CommandContext context) {
		Map<String, List<String>> databaseGroups = context.getAppConfig().getDatabaseGroups();
		if (databaseGroups != null) {
			List<String> selectedGroupNames = databaseGroups.get(databaseName);
			if (selectedGroupNames != null) {

				List<String> selectedHostNames = new ArrayList<>();

				if (logger.isInfoEnabled()) {
					logger.info(format("Creating forests on hosts in groups %s for database '%s'", selectedGroupNames, databaseName));
				}

				for (String groupName : selectedGroupNames) {
					List<String> groupHostNames = hostNameProvider.getGroupHostNames(groupName);
					if (groupHostNames != null && !groupHostNames.isEmpty()) {
						for (String hostName : groupHostNames) {
							// sanity check
							if (!selectedHostNames.contains(hostName)) {
								selectedHostNames.add(hostName);
							}
						}
					} else {
						logger.warn("No hosts found for group: " + groupName);
					}
				}

				Map<String, List<String>> databaseHosts = context.getAppConfig().getDatabaseHosts();
				if (databaseHosts != null) {
					List<String> set = databaseHosts.get(databaseName);
					if (set != null && !set.isEmpty()) {
						logger.warn(format("Database groups and database hosts were both specified for database '%s'; " +
							"only database groups are being used, database hosts will be ignored.", databaseName));
					}
				}

				if (logger.isInfoEnabled()) {
					logger.info(format("Creating forests on hosts %s based on groups %s for database '%s'", selectedHostNames, selectedGroupNames, databaseName));
				}

				return selectedHostNames;
			}
		}

		return null;
	}

	/**
	 * @param context
	 * @param hostNames
	 * @return
	 */
	protected List<String> determineHostNamesBasedOnDatabaseHosts(String databaseName, CommandContext context, List<String> hostNames) {
		Map<String, List<String>> databaseHosts = context.getAppConfig().getDatabaseHosts();
		if (databaseHosts != null) {
			List<String> databaseHostNames = databaseHosts.get(databaseName);
			if (databaseHostNames != null) {
				List<String> selectedHostNames = new ArrayList<>();
				for (String name : databaseHostNames) {
					if (hostNames.contains(name)) {
						selectedHostNames.add(name);
					} else {
						logger.warn(format("Host '%s' for database '%s' is not recognized, ignoring", name, databaseName));
					}
				}

				if (logger.isInfoEnabled()) {
					logger.info(format("Creating forests for database '%s' on hosts: %s", databaseName, selectedHostNames));
				}

				return selectedHostNames;
			}
		}
		return null;
	}
}
