package com.rjrudin.marklogic.appdeployer.command.security;

import java.io.File;

import com.rjrudin.marklogic.appdeployer.command.AbstractResourceCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.security.UserManager;

public class DeployUsersCommand extends AbstractResourceCommand {

    public DeployUsersCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_USERS);
        setUndoSortOrder(SortOrderConstants.DELETE_USERS);
    }

    protected File getResourcesDir(CommandContext context) {
        return new File(context.getAppConfig().getConfigDir().getSecurityDir(), "users");
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new UserManager(context.getManageClient());
    }

}
