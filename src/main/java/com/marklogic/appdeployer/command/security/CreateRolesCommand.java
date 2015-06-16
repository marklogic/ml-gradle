package com.marklogic.appdeployer.command.security;

import java.io.File;

import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.rest.mgmt.ResourceManager;
import com.marklogic.rest.mgmt.security.RoleManager;

public class CreateRolesCommand extends AbstractResourceCommand {

    @Override
    public Integer getExecuteSortOrder() {
        return SortOrderConstants.CREATE_ROLES;
    }

    protected File getResourcesDir(CommandContext context) {
        return new File(context.getAppConfig().getConfigDir().getSecurityDir(), "roles");
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new RoleManager(context.getManageClient());
    }

    @Override
    protected String getIdFieldName() {
        return "role-name";
    }

}
