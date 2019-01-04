package com.marklogic.appdeployer.command.schemas;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ext.schemasloader.SchemasLoader;
import com.marklogic.client.ext.schemasloader.impl.DefaultSchemasLoader;

import java.io.File;
import java.io.FileFilter;

public class LoadSchemasCommand extends AbstractCommand {

	public LoadSchemasCommand() {
		setExecuteSortOrder(SortOrderConstants.LOAD_SCHEMAS);
	}

	@Override
	public void execute(CommandContext context) {
		loadSchemasFromSchemasPath(context);
		loadSchemasFromDatabaseSpecificPaths(context);
	}

	/**
	 * This expects a single path to be defined in the AppConfig object. If set, then any files at that path are loaded
	 * into the schemas database defined by the AppConfig object.
	 *
	 * @param context
	 */
	protected void loadSchemasFromSchemasPath(CommandContext context) {
		AppConfig config = context.getAppConfig();
		final String schemasPath = config.getSchemasPath();
		if (schemasPath != null && schemasPath.trim().length() > 0) {
			loadSchemas(config.getSchemasPath(), config.getSchemasDatabaseName(), context);
		} else {
			logger.info("Schemas path is empty, so not attempting to load any schemas");
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
					loadSchemas(schemasDir.getAbsolutePath(), dir.getName(), context);
				}
			});
		});
	}

	protected void loadSchemas(String schemasPath, String schemasDatabaseName, CommandContext context) {
		logger.info(format("Loading schemas into database %s from: %s", schemasDatabaseName, schemasPath));
		DatabaseClient client = buildDatabaseClient(schemasDatabaseName, context);
		try {
			SchemasLoader schemasLoader = buildSchemasLoader(context, client);
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
	 *
	 * @param context
	 * @param client
	 * @return
	 */
	protected SchemasLoader buildSchemasLoader(CommandContext context, DatabaseClient client) {
		AppConfig config = context.getAppConfig();
		DefaultSchemasLoader schemasLoader = new DefaultSchemasLoader(client);
		FileFilter filter = config.getSchemasFileFilter();
		if (filter != null) {
			schemasLoader.addFileFilter(filter);
		}
		return schemasLoader;
	}
}
