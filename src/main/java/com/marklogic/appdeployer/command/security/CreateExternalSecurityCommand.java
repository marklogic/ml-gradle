package com.marklogic.appdeployer.command.security;

import java.io.File;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.rest.mgmt.ResourceManager;
import com.marklogic.rest.mgmt.security.ExternalSecurityManager;

public class CreateExternalSecurityCommand extends AbstractResourceCommand {

    public CreateExternalSecurityCommand() {
        setExecuteSortOrder(SortOrderConstants.CREATE_EXTERNAL_SECURITY);
    }

    @Override
    protected File getResourcesDir(CommandContext context) {
        return new File(context.getAppConfig().getConfigDir().getSecurityDir(), "external-security");
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new ExternalSecurityManager(context.getManageClient());
    }

}
