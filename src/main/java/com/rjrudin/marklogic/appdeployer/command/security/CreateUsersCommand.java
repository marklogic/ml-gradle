package com.rjrudin.marklogic.appdeployer.command.security;

import java.io.File;

import com.rjrudin.marklogic.appdeployer.command.AbstractResourceCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.security.UserManager;

public class CreateUsersCommand extends AbstractResourceCommand {

    public CreateUsersCommand() {
        setExecuteSortOrder(SortOrderConstants.CREATE_USERS);
    }

    /**
     * We usually want to delete users right before we delete roles, at the end of the deployment process.
     */
    @Override
    public Integer getUndoSortOrder() {
        return Integer.MAX_VALUE - 10;
    }

    protected File getResourcesDir(CommandContext context) {
        return new File(context.getAppConfig().getConfigDir().getSecurityDir(), "users");
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new UserManager(context.getManageClient());
    }

}
