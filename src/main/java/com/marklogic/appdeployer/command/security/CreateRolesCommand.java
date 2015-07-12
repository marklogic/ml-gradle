package com.marklogic.appdeployer.command.security;

import java.io.File;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.rest.mgmt.ResourceManager;
import com.marklogic.rest.mgmt.security.RoleManager;

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
