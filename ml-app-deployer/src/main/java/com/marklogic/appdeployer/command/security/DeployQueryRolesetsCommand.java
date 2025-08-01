/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.security;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.appdeployer.command.SupportsCmaCommand;
import com.marklogic.mgmt.api.configuration.Configuration;
import com.marklogic.mgmt.api.security.queryroleset.QueryRoleset;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.QueryRolesetManager;

import java.io.File;

public class DeployQueryRolesetsCommand extends AbstractResourceCommand implements SupportsCmaCommand {

	public DeployQueryRolesetsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_QUERY_ROLESETS);
		setUndoSortOrder(SortOrderConstants.DELETE_QUERY_ROLESETS);

		setResourceClassType(QueryRoleset.class);
	}

	@Override
	public boolean cmaShouldBeUsed(CommandContext context) {
		return context.getAppConfig().getCmaConfig().isDeployQueryRolesets();
	}

	@Override
	public void addResourceToConfiguration(ObjectNode resource, Configuration configuration) {
		configuration.addQueryRoleset(resource);
	}

	@Override
	protected void deployConfiguration(CommandContext context, Configuration config) {
		if (context.getAppConfig().getCmaConfig().isCombineRequests()) {
			logger.info("Adding query rolesets to combined CMA request");
			context.addCmaConfigurationToCombinedRequest(config);
		} else {
			super.deployConfiguration(context, config);
		}
	}

	@Override
	protected File[] getResourceDirs(CommandContext context) {
		return findResourceDirs(context, configDir -> configDir.getQueryRolesetsDir());
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return new QueryRolesetManager(context.getManageClient());
	}
}
