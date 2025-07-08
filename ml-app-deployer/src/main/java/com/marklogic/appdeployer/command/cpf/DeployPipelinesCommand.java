/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.cpf;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.cpf.AbstractCpfResourceManager;
import com.marklogic.mgmt.resource.cpf.PipelineManager;

import java.io.File;

public class DeployPipelinesCommand extends AbstractCpfResourceCommand {

	public DeployPipelinesCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_PIPELINES);
	}

	@Override
	protected File getCpfResourceDir(ConfigDir configDir) {
		return configDir.getPipelinesDir();
	}

	@Override
	protected AbstractCpfResourceManager getResourceManager(CommandContext context, String databaseIdOrName) {
		return new PipelineManager(context.getManageClient(), databaseIdOrName);
	}
}
