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

import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.api.forest.Forest;
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
	public ForestHostNames calculateHostNames(String databaseName, CommandContext context, List<Forest> existingPrimaryForests) {
		final List<String> candidateHostNames = getCandidateHostNames(databaseName, context);
		if (candidateHostNames.isEmpty()) {
			throw new RuntimeException("Unable to determine host names for forests for database: " + databaseName + "; please check the " +
				"properties you've set for creating forests for this database to ensure that forests can be created on at " +
				"least one host in your cluster");
		}

		final List<String> primaryForestHostNames = new ArrayList<>();
		final List<String> replicaForestHostNames = new ArrayList<>();

		if (context.getAppConfig().isDatabaseWithForestsOnOneHost(databaseName)) {
			if (existingPrimaryForests.size() > 0) {
				primaryForestHostNames.add(existingPrimaryForests.get(0).getHost());
				replicaForestHostNames.addAll(candidateHostNames);
			} else {
				primaryForestHostNames.add(candidateHostNames.get(0));
				replicaForestHostNames.addAll(candidateHostNames);
			}
		} else {
			primaryForestHostNames.addAll(candidateHostNames);
			replicaForestHostNames.addAll(candidateHostNames);
		}

		return new ForestHostNames(primaryForestHostNames, replicaForestHostNames);
	}

	protected List<String> getCandidateHostNames(String databaseName, CommandContext context) {
		if (logger.isInfoEnabled()) {
			logger.info("Finding eligible hosts for forests for database: " + databaseName);
		}

		List<String> hostNamesFromDatabaseGroups = determineHostNamesBasedOnDatabaseGroups(databaseName, context);
		if (hostNamesFromDatabaseGroups != null) {
			return hostNamesFromDatabaseGroups;
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
	protected List<String> determineHostNamesBasedOnDatabaseGroups(String databaseName, CommandContext context) {
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
