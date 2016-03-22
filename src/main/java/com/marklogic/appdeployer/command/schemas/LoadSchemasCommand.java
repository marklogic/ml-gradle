package com.marklogic.appdeployer.command.schemas;

import java.io.File;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.schemasloader.SchemasLoader;
import com.marklogic.client.schemasloader.impl.DefaultSchemasFinder;
import com.marklogic.client.schemasloader.impl.DefaultSchemasLoader;

public class LoadSchemasCommand extends AbstractCommand  {

	private SchemasLoader schemasLoader;
	
	public LoadSchemasCommand() {
        setExecuteSortOrder(SortOrderConstants.LOAD_SCHEMAS);
	}
	
	@Override
	public void execute(CommandContext context) {
		loadSchemasIntoSchemasDatabase(context);
	}

	protected void loadSchemasIntoSchemasDatabase(CommandContext context) {
		if (schemasLoader == null) {
			initializeDefaultSchemasLoader(context);
		}
		
		
		AppConfig config = context.getAppConfig();
        DatabaseClient client = config.newSchemasDatabaseClient();
        try {
        	String schemasPath = config.getSchemasPath();
            
        	logger.info("Loading schemas database data from path: " + schemasPath);
        	schemasLoader.loadSchemas(new File(schemasPath), new DefaultSchemasFinder(), client);
            
        } finally {
            client.release();
        }
	}

	private void initializeDefaultSchemasLoader(CommandContext context) {
	     logger.info("Initializing instance of DefaultSchemasLoader");
	     this.schemasLoader = new DefaultSchemasLoader();
	}

}
