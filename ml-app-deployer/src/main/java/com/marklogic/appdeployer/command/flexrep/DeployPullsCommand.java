/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.flexrep;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.flexrep.PullManager;

import java.io.File;

public class DeployPullsCommand extends AbstractResourceCommand {

	private ResourceManager pullManager;

	public DeployPullsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_FLEXREP_PULLS);
	}

	@Override
	protected File[] getResourceDirs(CommandContext context) {
		return findResourceDirs(context, configDir -> configDir.getFlexrepPullsDir());
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return pullManager;
	}

	@Override
	public void execute(CommandContext context) {
		AppConfig appConfig = context.getAppConfig();
		for (ConfigDir configDir : appConfig.getConfigDirs()) {
			deployFlexRepPulls(context, configDir, appConfig.getContentDatabaseName());
			for (File dir : configDir.getDatabaseResourceDirectories()) {
				String databaseName = determineDatabaseNameForDatabaseResourceDirectory(context, configDir, dir);
				if (databaseName != null) {
					deployFlexRepPulls(context, new ConfigDir(dir), databaseName);
				}
			}
		}
	}

	protected void deployFlexRepPulls(CommandContext context, ConfigDir configDir, String databaseIdOrName) {
		pullManager = new PullManager(context.getManageClient(), databaseIdOrName);
		processExecuteOnResourceDir(context, configDir.getFlexrepPullsDir());
	}


}
