/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.modules;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.client.DatabaseClient;

public class DeleteModulesCommand extends AbstractCommand {

	private String pattern;
	private String databaseName;

	public DeleteModulesCommand() {
		super();
	}

	public DeleteModulesCommand(String pattern) {
		this();
		this.pattern = pattern;
	}

	@Override
	public void execute(CommandContext context) {
		if (pattern == null || pattern.trim().length() == 0) {
			logger.warn("No pattern was specified, so not deleting any modules");
		}

		AppConfig appConfig = context.getAppConfig();

		String dbName = databaseName != null ? databaseName : appConfig.getModulesDatabaseName();
		if (logger.isInfoEnabled()) {
			logger.info(format("Deleting modules in database '%s' with URIs matching pattern '%s'", dbName, pattern));
		}

		DatabaseClient client = appConfig.newAppServicesDatabaseClient(dbName);

		String xquery = "for $uri in cts:uri-match('%s') where fn:doc-available($uri) return xdmp:document-delete($uri)";
		try {
			client.newServerEval().xquery(format(xquery, pattern)).evalAs(String.class);
			if (logger.isInfoEnabled()) {
				logger.info("Finished deleting modules");
			}
		} finally {
			client.release();
		}
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
}
