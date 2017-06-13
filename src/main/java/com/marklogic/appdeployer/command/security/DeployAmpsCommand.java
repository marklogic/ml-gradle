package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.ResourceManager;
import com.marklogic.mgmt.security.AmpManager;

import java.io.File;

public class DeployAmpsCommand extends AbstractResourceCommand {

    public DeployAmpsCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_AMPS);
        setUndoSortOrder(SortOrderConstants.DELETE_AMPS);
    }

    @Override
    protected File[] getResourceDirs(CommandContext context) {
        return new File[] { new File(context.getAppConfig().getConfigDir().getSecurityDir(), "amps") };
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new AmpManager(context.getManageClient());
    }

}
