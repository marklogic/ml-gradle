package com.marklogic.appdeployer.command.security;

import java.io.File;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.ResourceManager;
import com.marklogic.mgmt.security.RoleManager;

public class DeployRolesCommand extends AbstractResourceCommand {

    public DeployRolesCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_ROLES);
        setUndoSortOrder(SortOrderConstants.DELETE_ROLES);
    }

    protected File[] getResourceDirs(CommandContext context) {
        return new File[] { new File(context.getAppConfig().getConfigDir().getSecurityDir(), "roles") };
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new RoleManager(context.getManageClient());
    }

}
