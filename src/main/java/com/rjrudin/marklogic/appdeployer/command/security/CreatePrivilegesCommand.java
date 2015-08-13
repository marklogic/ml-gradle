package com.rjrudin.marklogic.appdeployer.command.security;

import java.io.File;

import com.rjrudin.marklogic.appdeployer.command.AbstractResourceCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.security.PrivilegeManager;

public class CreatePrivilegesCommand extends AbstractResourceCommand {

    public CreatePrivilegesCommand() {
        setExecuteSortOrder(SortOrderConstants.CREATE_PRIVILEGES);
        setUndoSortOrder(SortOrderConstants.DELETE_PRIVILEGES);
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
