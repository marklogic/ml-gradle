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
package com.marklogic.appdeployer.command.schemas;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ext.schemasloader.SchemasLoader;
import com.marklogic.client.ext.schemasloader.impl.DefaultSchemasLoader;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.database.Database;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.mapper.ResourceMapper;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

public class LoadSchemasCommand extends AbstractCommand {

	public LoadSchemasCommand() {
		setExecuteSortOrder(SortOrderConstants.LOAD_SCHEMAS);
	}

	@Override
	public void execute(CommandContext context) {
		loadSchemasFromSchemaPaths(context);
		loadSchemasFromDatabaseSpecificPaths(context);
	}

	/**
	 * @param context
	 */
	protected void loadSchemasFromSchemaPaths(CommandContext context) {
		AppConfig config = context.getAppConfig();
		List<String> schemaPaths = config.getSchemaPaths();
		if (schemaPaths != null && !schemaPaths.isEmpty()) {
			for (String path : schemaPaths) {
				loadSchemas(path, config.getSchemasDatabaseName(), context);
			}
		}
	}

	/**
	 * This loads schemas from every "databases/(name of schemas database)/schemas" path found in each configuration
	 * directory in the AppConfig object.
	 *
	 * @param context
	 */
	protected void loadSchemasFromDatabaseSpecificPaths(CommandContext context) {
		context.getAppConfig().getConfigDirs().forEach(configDir -> {
			configDir.getDatabaseResourceDirectories().forEach(dir -> {
				File schemasDir = new File(dir, "schemas");
				if (schemasDir.exists() && schemasDir.listFiles().length > 0) {
					String databaseName = determineDatabaseNameForDatabaseResourceDirectory(context, configDir, dir);
					if (databaseName != null) {
						loadSchemas(schemasDir.getAbsolutePath(), databaseName, context);
					}
				}
			});
		});
	}

	protected void loadSchemas(String schemasPath, String schemasDatabaseName, CommandContext context) {
		logger.info(format("Loading schemas into database %s from: %s", schemasDatabaseName, schemasPath));
		DatabaseClient schemasClient = context.getAppConfig().newAppServicesDatabaseClient(schemasDatabaseName);
		DatabaseClient contentClient = buildContentClient(context, schemasDatabaseName);
		try {
			SchemasLoader schemasLoader = buildSchemasLoader(context, schemasClient, contentClient);
			schemasLoader.loadSchemas(schemasPath);
			logger.info("Finished loading schemas from: " + schemasPath);
		} catch (FailedRequestException fre) {
			if (fre.getMessage().contains("NOSUCHDB")) {
				logger.warn("Unable to load schemas because no schemas database exists; cause: " + fre.getMessage());
			} else {
				throw fre;
			}
		} finally {
			schemasClient.release();
			if (contentClient != null) {
				contentClient.release();
			}
		}
	}

	/**
	 * Construct a content client, for use when validating TDEs and generating QBVs.
	 *
	 * @param context
	 * @param schemasDatabase
	 * @return
	 */
	private DatabaseClient buildContentClient(CommandContext context, String schemasDatabase) {
		String contentDatabase = findContentDatabaseAssociatedWithSchemasDatabase(context, schemasDatabase);
		if (contentDatabase != null) {
			logger.info(format("Will use %s as a content database when loading into schemas database: %s", contentDatabase, schemasDatabase));
			return context.getAppConfig().newAppServicesDatabaseClient(contentDatabase);
		}
		logger.warn(format("Unable to find a content database associated with schemas database: %s; this may " +
			"result in errors when loading TDE templates and Query-Based-View scripts.", schemasDatabase));
		return null;
	}

	/**
	 * Will utilize schemasFileFilter in AppConfig if it's been set.
	 * <p>
	 * So given a schemasDatabaseName,
	 *
	 * @param context
	 * @param schemasClient
	 * @return
	 */
	protected SchemasLoader buildSchemasLoader(CommandContext context, DatabaseClient schemasClient, DatabaseClient contentClient) {
		AppConfig appConfig = context.getAppConfig();
		DefaultSchemasLoader schemasLoader = new DefaultSchemasLoader(schemasClient, contentClient, context.getAppConfig().isTdeValidationEnabled());
		schemasLoader.setCascadeCollections(appConfig.isCascadeCollections());
		schemasLoader.setCascadePermissions(appConfig.isCascadePermissions());
		FileFilter filter = appConfig.getSchemasFileFilter();
		if (filter != null) {
			schemasLoader.addFileFilter(filter);
		}

		// TODO Should rename this method to something more generic than "modules"; not sure what that should be yet
		if (appConfig.isReplaceTokensInModules()) {
			schemasLoader.setTokenReplacer(appConfig.buildTokenReplacer());
		}

		return schemasLoader;
	}

	/**
	 * In order to validate a TDE template, a content database must be found that is associated with the given
	 * schemas database. This method looks at the databases directory in each config directory and tries to find a
	 * database resource file that defines a database associated with the given schemas database.
	 *
	 * @param context
	 * @param schemasDatabaseName
	 * @return
	 */
	protected String findContentDatabaseAssociatedWithSchemasDatabase(CommandContext context, String schemasDatabaseName) {
		String tdeValidationDatabase = null;
		ResourceMapper resourceMapper = new DefaultResourceMapper(new API(context.getManageClient()));
		for (ConfigDir configDir : context.getAppConfig().getConfigDirs()) {
			File dbDir = configDir.getDatabasesDir();
			if (dbDir != null && dbDir.exists()) {
				for (File f : listFilesInDirectory(dbDir)) {
					String payload = copyFileToString(f, context);
					try {
						Database db = resourceMapper.readResource(payload, Database.class);
						if (schemasDatabaseName.equals(db.getSchemaDatabase())) {
							tdeValidationDatabase = db.getDatabaseName();
							break;
						}
					} catch (Exception ex) {
						logger.warn("Unexpected error when reading database file to determine database for TDE validation: " + ex.getMessage());
					}
				}
			}
		}
		return tdeValidationDatabase;
	}
}
