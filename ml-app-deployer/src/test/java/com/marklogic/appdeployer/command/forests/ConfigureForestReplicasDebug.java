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

import java.util.Map;
import java.util.Set;

/**
 * Not an actual test, as this depends on an environment with multiple hosts, which is normally not the case on a
 * development machine.
 * <p>
 * This is now intended to run against the multi-host cluster setup via docker-compose.yml.
 */
public class ConfigureForestReplicasDebug {

	public static void main(String[] args) {
		final String host = "localhost"; //args[0];
		final String password = "admin"; //args[1];
		final int managePort = 8102;
		final String dbName = "testdb";
		final int replicaCount = 2;

		ManageClient manageClient = new ManageClient(new ManageConfig(host, managePort, "admin", password));

		final AppConfig appConfig = new AppConfig();
		appConfig.setDatabasesWithForestsOnOneHost(Set.of(dbName));
		appConfig.setDatabaseNamesAndReplicaCounts(Map.of(dbName, replicaCount));

		final CommandContext context = new CommandContext(appConfig, manageClient, null);

		DeployDatabaseCommand deployDatabaseCommand = new DeployDatabaseCommand();
		deployDatabaseCommand.setForestsPerHost(2);
		deployDatabaseCommand.setCreateDatabaseWithoutFile(true);
		deployDatabaseCommand.setDatabaseName(dbName);

		// Deploy the database.
		deployDatabaseCommand.execute(context);

		// Configure replicas.
		ConfigureForestReplicasCommand configureForestReplicasCommand = new ConfigureForestReplicasCommand();
		configureForestReplicasCommand.execute(context);
		// Deploy again to make sure there are no errors
		configureForestReplicasCommand.execute(context);

		// Delete replicas, and then the database.
		configureForestReplicasCommand.undo(context);
		deployDatabaseCommand.undo(context);
	}
}
