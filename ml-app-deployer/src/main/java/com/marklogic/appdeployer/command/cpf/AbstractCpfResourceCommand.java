/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.cpf;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.mgmt.resource.cpf.AbstractCpfResourceManager;

import java.io.File;

/**
 * Base class for commands that deploy CPF resources. By default, this will use getCpfDatabaseName on AppConfig to
 * determine what database to deploy CPF resources to. That can be overridden via the setDatabaseIdOrName method
 * on this class.
 */
public abstract class AbstractCpfResourceCommand extends AbstractCommand {

	private String databaseIdOrName;

	protected abstract File getCpfResourceDir(ConfigDir configDir);

	protected abstract AbstractCpfResourceManager getResourceManager(CommandContext context, String databaseIdOrName);

	@Override
	public void execute(CommandContext context) {
		AppConfig config = context.getAppConfig();
		for (ConfigDir configDir : config.getConfigDirs()) {
			File dir = getCpfResourceDir(configDir);
			if (dir.exists()) {
				final String db = databaseIdOrName != null ? databaseIdOrName : config.getCpfDatabaseName();
				AbstractCpfResourceManager mgr = getResourceManager(context, db);
				for (File f : listFilesInDirectory(dir)) {
					String payload = copyFileToString(f, context);
					mgr.save(payload);
				}
			} else {
				logResourceDirectoryNotFound(dir);
			}
		}
	}

	public void setDatabaseIdOrName(String databaseIdOrName) {
		this.databaseIdOrName = databaseIdOrName;
	}
}
