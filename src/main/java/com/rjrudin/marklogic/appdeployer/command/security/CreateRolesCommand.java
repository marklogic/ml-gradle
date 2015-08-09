package com.rjrudin.marklogic.appdeployer.command.security;

import java.io.File;

import com.rjrudin.marklogic.appdeployer.command.AbstractResourceCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.security.RoleManager;

public class CreateRolesCommand extends AbstractResourceCommand {

    public CreateRolesCommand() {
        setExecuteSortOrder(SortOrderConstants.CREATE_ROLES);
    }

    /**
     * Roles are usually the very last thing we want to delete, right after deleting users.
     */
    @Override
    public Integer getUndoSortOrder() {
        return Integer.MAX_VALUE;
    }

    protected File getResourcesDir(CommandContext context) {
        return new File(context.getAppConfig().getConfigDir().getSecurityDir(), "roles");
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new RoleManager(context.getManageClient());
    }

}
