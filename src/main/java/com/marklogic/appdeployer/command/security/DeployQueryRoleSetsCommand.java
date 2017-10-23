package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.QueryRoleSetsManager;

import java.io.File;

public class DeployQueryRoleSetsCommand extends AbstractResourceCommand {

	public DeployQueryRoleSetsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_QUERY_ROLE_SETS);
		setUndoSortOrder(SortOrderConstants.DELETE_QUERY_ROLE_SETS);
	}
	@Override
	protected File[] getResourceDirs(CommandContext context) {
		return new File[] { context.getAppConfig().getConfigDir().getQueryRoleSetsDir() };
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return new QueryRoleSetsManager(context.getManageClient());
	}
}
