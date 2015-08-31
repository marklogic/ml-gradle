package com.rjrudin.marklogic.appdeployer.command.security;

import java.io.File;

import com.rjrudin.marklogic.appdeployer.command.AbstractResourceCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.security.ExternalSecurityManager;

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
