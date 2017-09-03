package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.UserManager;

import java.io.File;

import java.io.File;

import java.io.File;

public class DeployUsersCommand extends AbstractResourceCommand {

    public DeployUsersCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_USERS);
        setUndoSortOrder(SortOrderConstants.DELETE_USERS);
    }

    protected File[] getResourceDirs(CommandContext context) {
        return new File[] { context.getAppConfig().getConfigDir().getUsersDir() };
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new UserManager(context.getManageClient());
    }

}
