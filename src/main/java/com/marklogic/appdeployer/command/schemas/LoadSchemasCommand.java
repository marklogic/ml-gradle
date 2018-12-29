package com.marklogic.appdeployer.command.schemas;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ext.schemasloader.SchemasLoader;
import com.marklogic.client.ext.schemasloader.impl.DefaultSchemasLoader;

import java.io.FileFilter;

public class LoadSchemasCommand extends AbstractCommand {

	public LoadSchemasCommand() {
		setExecuteSortOrder(SortOrderConstants.LOAD_SCHEMAS);
	}

	@Override
	public void execute(CommandContext context) {
		loadSchemasIntoSchemasDatabase(context);
	}

	protected void loadSchemasIntoSchemasDatabase(CommandContext context) {
		AppConfig config = context.getAppConfig();
		DatabaseClient client = config.newSchemasDatabaseClient();
		SchemasLoader schemasLoader = buildSchemasLoader(context, client);
		try {
			String schemasPath = config.getSchemasPath();
			if (schemasPath != null && schemasPath.trim().length() > 0) {
				logger.info("Loading schemas from path: " + schemasPath);
				schemasLoader.loadSchemas(schemasPath);
				logger.info("Finished loading schemas from: " + schemasPath);
			}
			else {
				logger.info("Schemas path is empty, so not attempting to load any schemas");
			}
		} catch (FailedRequestException fre) {
			if (fre.getMessage().contains("NOSUCHDB")) {
				logger.warn("Unable to load schemas because no schemas database exists; cause: " + fre.getMessage());
			}
			else {
				throw fre;
			}
		} finally {
			client.release();
		}
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
