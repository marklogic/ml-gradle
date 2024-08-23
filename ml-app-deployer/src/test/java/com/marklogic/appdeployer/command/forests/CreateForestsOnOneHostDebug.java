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

import java.util.HashSet;
import java.util.Set;

/**
 * Debug program for verifying that a forest is only created on one host. Gotta run this against a cluster with 2 or
 * more hosts.
 */
public class CreateForestsOnOneHostDebug {

	public static void main(String[] args) {
		final String host = args[0];
		final String password = args[1];

		ManageConfig config = new ManageConfig(host, 8002, "admin", password);
		ManageClient manageClient = new ManageClient(config);

		AppConfig appConfig = new AppConfig();
		Set<String> names = new HashSet<>();
		names.add("testdb");
		appConfig.setDatabasesWithForestsOnOneHost(names);
		CommandContext context = new CommandContext(appConfig, manageClient, null);

		DeployDatabaseCommand ddc = new DeployDatabaseCommand();
		ddc.setForestsPerHost(1);
		ddc.setCreateDatabaseWithoutFile(true);
		ddc.setDatabaseName("testdb");

		ddc.execute(context);
		ddc.undo(context);
	}
}
