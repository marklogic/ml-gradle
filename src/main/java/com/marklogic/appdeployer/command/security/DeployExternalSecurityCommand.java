package com.marklogic.appdeployer.command.security;

import java.io.File;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.ResourceManager;
import com.marklogic.mgmt.security.ExternalSecurityManager;

public class DeployExternalSecurityCommand extends AbstractResourceCommand {

    public DeployExternalSecurityCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_EXTERNAL_SECURITY);
        setUndoSortOrder(SortOrderConstants.DELETE_EXTERNAL_SECURITY);
    }

    @Override
    protected File[] getResourceDirs(CommandContext context) {
        return new File[] { new File(context.getAppConfig().getConfigDir().getSecurityDir(), "external-security") };
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new ExternalSecurityManager(context.getManageClient());
    }

}
