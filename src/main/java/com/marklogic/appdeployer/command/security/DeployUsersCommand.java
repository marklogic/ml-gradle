package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.appdeployer.command.SupportsCmaCommand;
import com.marklogic.mgmt.api.configuration.Configuration;
import com.marklogic.mgmt.api.security.User;
import com.marklogic.mgmt.mapper.ResourceMapper;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.UserManager;

import java.io.File;

public class DeployUsersCommand extends AbstractResourceCommand implements SupportsCmaCommand {

	public DeployUsersCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_USERS);
		setUndoSortOrder(SortOrderConstants.DELETE_USERS);
	}

	protected File[] getResourceDirs(CommandContext context) {
		return findResourceDirs(context.getAppConfig(), configDir -> configDir.getUsersDir());
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return new UserManager(context.getManageClient());
	}

	@Override
	public boolean cmaShouldBeUsed(CommandContext context) {
		return context.getAppConfig().isDeployUsersWithCma();
	}

	@Override
	public void addResourceToConfiguration(String payload, ResourceMapper resourceMapper, Configuration configuration) {
		configuration.addUser(resourceMapper.readResource(payload, User.class));
	}
}
