package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.ProtectedPathManager;
import com.marklogic.mgmt.resource.security.UserManager;

import java.io.File;

public class DeployProtectedPathsCommand extends AbstractResourceCommand {

	public DeployProtectedPathsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_PROTECTED_PATHS);
		setUndoSortOrder(SortOrderConstants.DELETE_PROTECTED_PATHS);
	}

	@Override
	protected File[] getResourceDirs(CommandContext context) {
		return findResourceDirs(context, configDir -> configDir.getProtectedPathsDir());
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return new ProtectedPathManager(context.getManageClient());
	}
}
