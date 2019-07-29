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
				if (schemasDir.exists()) {
					String databaseName = determineDatabaseNameForDatabaseResourceDirectory(context, configDir, dir);
					loadSchemas(schemasDir.getAbsolutePath(), databaseName, context);
				}
			});
		});
	}

	protected void loadSchemas(String schemasPath, String schemasDatabaseName, CommandContext context) {
		logger.info(format("Loading schemas into database %s from: %s", schemasDatabaseName, schemasPath));
		DatabaseClient client = buildDatabaseClient(schemasDatabaseName, context);
		try {
			SchemasLoader schemasLoader = buildSchemasLoader(context, client, schemasDatabaseName);
			schemasLoader.loadSchemas(schemasPath);
			logger.info("Finished loading schemas from: " + schemasPath);
		} catch (FailedRequestException fre) {
			if (fre.getMessage().contains("NOSUCHDB")) {
				logger.warn("Unable to load schemas because no schemas database exists; cause: " + fre.getMessage());
			} else {
				throw fre;
			}
		} finally {
			client.release();
		}
	}

	protected DatabaseClient buildDatabaseClient(String schemasDatabaseName, CommandContext context) {
		return context.getAppConfig().newAppServicesDatabaseClient(schemasDatabaseName);
	}

	/**
	 * Will utilize schemasFileFilter in AppConfig if it's been set.
	 * <p>
	 * So given a schemasDatabaseName,
	 *
	 * @param context
	 * @param client
	 * @return
	 */
	protected SchemasLoader buildSchemasLoader(CommandContext context, DatabaseClient client, String schemasDatabaseName) {
		AppConfig appConfig = context.getAppConfig();

		String tdeValidationDatabase = null;
		if (appConfig.isTdeValidationEnabled()) {
			tdeValidationDatabase = findContentDatabaseAssociatedWithSchemasDatabase(context, schemasDatabaseName);
			if (tdeValidationDatabase != null) {
				logger.info(format("TDE templates loaded into %s will be validated against content database %s",
					schemasDatabaseName, tdeValidationDatabase));
			}
		} else {
			logger.info("TDE validation is disabled");
		}

		DefaultSchemasLoader schemasLoader = new DefaultSchemasLoader(client, tdeValidationDatabase);
		FileFilter filter = appConfig.getSchemasFileFilter();
		if (filter != null) {
			schemasLoader.addFileFilter(filter);
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
