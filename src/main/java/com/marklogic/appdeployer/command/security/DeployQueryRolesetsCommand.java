package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.QueryRolesetsManager;

import java.io.File;

public class DeployQueryRolesetsCommand extends AbstractResourceCommand {

	public DeployQueryRolesetsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_QUERY_ROLESETS);
		setUndoSortOrder(SortOrderConstants.DELETE_QUERY_ROLESETS);
	}

	@Override
	protected File[] getResourceDirs(CommandContext context) {
		return findResourceDirs(context, configDir -> configDir.getQueryRolesetsDir());
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return new QueryRolesetsManager(context.getManageClient());
	}
}
