package com.rjrudin.marklogic.appdeployer.command.security;

import java.io.File;

import com.rjrudin.marklogic.appdeployer.command.AbstractResourceCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.security.RoleManager;

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
