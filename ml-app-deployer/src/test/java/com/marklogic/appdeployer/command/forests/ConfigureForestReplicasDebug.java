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
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.databases.DeployDatabaseCommand;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.mgmt.resource.hosts.HostManager;

import java.util.*;

/**
 * Not an actual test, as this depends on an environment with multiple hosts, which is normally not the case on a
 * development machine.
 */
public class ConfigureForestReplicasDebug {

	public static void main(String[] args) {
		final String host = "localhost"; //args[0];
		final String password = "admin"; //args[1];

		final String dbName = "testdb";

		ManageConfig config = new ManageConfig(host, 8002, "admin", password);
		ManageClient manageClient = new ManageClient(config);

		AppConfig appConfig = new AppConfig();
		Map<String, Integer> map = new HashMap<>();
		map.put(dbName, 2);
		appConfig.setDatabaseNamesAndReplicaCounts(map);

		List<String> hostNames = new HostManager(manageClient).getHostNames();

		Map<String, List<String>> databaseHosts = new LinkedHashMap<>();
		List<String> hosts = new ArrayList<>();
		hosts.add(hostNames.get(0));
		hosts.add(hostNames.get(1));
		databaseHosts.put(dbName, hosts);
		//appConfig.setDatabaseHosts(databaseHosts);

		Map<String, List<String>> databaseGroups = new LinkedHashMap<>();
		List<String> groups = new ArrayList<>();
		groups.add("Default");
		databaseGroups.put(dbName, groups);
		//appConfig.setDatabaseGroups(databaseGroups);

		CommandContext context = new CommandContext(appConfig, manageClient, null);

		DeployDatabaseCommand ddc = new DeployDatabaseCommand();
		ddc.setForestsPerHost(2);
		ddc.setCreateDatabaseWithoutFile(true);
		ddc.setDatabaseName(dbName);

		ConfigureForestReplicasCommand cfrc = new ConfigureForestReplicasCommand();

		// Deploy the database, and then configure replicas
		ddc.execute(context);
		cfrc.execute(context);

		// Deploy again to make sure there are no errors
		cfrc.execute(context);

		// Then delete the replicas, and then undeploy the database
		cfrc.undo(context);
		ddc.undo(context);
	}
}
