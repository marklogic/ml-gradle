package com.marklogic.appdeployer.command.schemas;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ext.schemasloader.impl.DefaultSchemasLoader;

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
		DefaultSchemasLoader schemasLoader = new DefaultSchemasLoader(client);
		try {
			String schemasPath = config.getSchemasPath();
			logger.info("Loading schemas from path: " + schemasPath);
			schemasLoader.loadSchemas(schemasPath);
			logger.info("Finished loading schemas from: " + schemasPath);
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
}
