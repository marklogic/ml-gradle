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
package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractUndoableCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.mgmt.resource.databases.DatabaseManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DeploySubDatabasesCommand extends AbstractUndoableCommand {

	private DeployDatabaseCommandFactory deployDatabaseCommandFactory;
	private String superDatabaseName;

	/**
	 * @param superDatabaseName
	 * @param deployDatabaseCommandFactory
	 */
	public DeploySubDatabasesCommand(String superDatabaseName, DeployDatabaseCommandFactory deployDatabaseCommandFactory) {
		this.superDatabaseName = superDatabaseName;
		this.deployDatabaseCommandFactory = deployDatabaseCommandFactory;
	}

	/**
	 * Creates and attaches sub-databases to a the specified database, making it a super-database.
	 *
	 * @param context
	 */
	public void execute(CommandContext context) {
		for (ConfigDir configDir : context.getAppConfig().getConfigDirs()) {
			File subdbDir = new File(configDir.getDatabasesDir() + File.separator + "subdatabases" + File.separator + superDatabaseName);

			if (logger.isDebugEnabled()) {
				logger.debug(format("Checking for sub-databases in: %s for database: %s", subdbDir.getAbsolutePath(), superDatabaseName));
			}

			if (subdbDir.exists()) {
				List<String> subDbNames = new ArrayList<>();
				for (File subDatabaseFile : listFilesInDirectory(subdbDir)) {
					logger.info(format("Processing sub-database for %s found in file: %s", superDatabaseName, subDatabaseFile.getAbsolutePath()));

					DeployDatabaseCommand subDbCommand = this.deployDatabaseCommandFactory.newDeployDatabaseCommand(subDatabaseFile);
					// Sub databases should be created immediately so they can then be attached
					subDbCommand.setSupportsResourceMerging(false);
					subDbCommand.setDatabaseFile(subDatabaseFile);
					subDbCommand.setSuperDatabaseName(superDatabaseName);
					subDbCommand.setSubDatabase(true);
					subDbCommand.execute(context);
					subDbNames.add(subDbCommand.getDatabaseName());
					logger.info(format("Created sub-database %s for database %s", subDbCommand.getDatabaseName(), superDatabaseName));
				}

				if (subDbNames.size() > 0) {
					new DatabaseManager(context.getManageClient()).attachSubDatabases(superDatabaseName, subDbNames);
				}
			}
		}
	}

	@Override
	public void undo(CommandContext context) {
		DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());

		for (ConfigDir configDir : context.getAppConfig().getConfigDirs()) {
			File subdbDir = new File(configDir.getDatabasesDir() + File.separator + "subdatabases" + File.separator + superDatabaseName);

			if (logger.isDebugEnabled()) {
				logger.debug(format("Checking to see if %s has sub-databases that need to be removed. Looking in: %s", superDatabaseName, subdbDir.getAbsolutePath()));
			}

			if (subdbDir.exists()) {
				logger.info("Removing all sub-databases from database: " + superDatabaseName);
				dbMgr.detachSubDatabases(superDatabaseName);
				for (File f : listFilesInDirectory(subdbDir)) {
					DeployDatabaseCommand subDbCommand = this.deployDatabaseCommandFactory.newDeployDatabaseCommand(null);
					subDbCommand.setDatabaseFile(f);
					subDbCommand.setSuperDatabaseName(superDatabaseName);
					subDbCommand.setSubDatabase(true);
					subDbCommand.undo(context);
				}
			}
		}
	}
}
