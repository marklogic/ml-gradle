package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.appdeployer.command.SupportsCmaCommand;
import com.marklogic.mgmt.api.configuration.Configuration;
import com.marklogic.mgmt.api.security.Privilege;
import com.marklogic.mgmt.mapper.ResourceMapper;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.PrivilegeManager;

import java.io.File;

import java.io.File;

import java.io.File;

public class DeployPrivilegesCommand extends AbstractResourceCommand implements SupportsCmaCommand {

    public DeployPrivilegesCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_PRIVILEGES);
        setUndoSortOrder(SortOrderConstants.DELETE_PRIVILEGES);
    }

    @Override
    protected File[] getResourceDirs(CommandContext context) {
    	return findResourceDirs(context, configDir -> configDir.getPrivilegesDir());
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new PrivilegeManager(context.getManageClient());
    }

	@Override
	public boolean cmaShouldBeUsed(CommandContext context) {
		return context.getAppConfig().isDeployPrivilegesWithCma();
	}

	@Override
	public void addResourceToConfiguration(String payload, ResourceMapper resourceMapper, Configuration configuration) {
    	configuration.addPrivilege(resourceMapper.readResource(payload, Privilege.class));
	}
}
