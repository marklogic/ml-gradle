package com.marklogic.appdeployer.command.security;

import java.io.File;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.rest.mgmt.ResourceManager;
import com.marklogic.rest.mgmt.security.PrivilegeManager;

public class CreatePrivilegesCommand extends AbstractResourceCommand {

    @Override
    public Integer getExecuteSortOrder() {
        return SortOrderConstants.CREATE_PRIVILEGES;
    }

    @Override
    protected File getResourcesDir(CommandContext context) {
        return new File(context.getAppConfig().getConfigDir().getSecurityDir(), "privileges");
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new PrivilegeManager(context.getManageClient());
    }

}
