package com.rjrudin.marklogic.appdeployer.command.security;

import java.io.File;

import com.rjrudin.marklogic.appdeployer.command.AbstractResourceCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.security.AmpManager;

public class DeployAmpsCommand extends AbstractResourceCommand {

    public DeployAmpsCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_AMPS);
        setUndoSortOrder(SortOrderConstants.DELETE_AMPS);
    }

    @Override
    protected File getResourcesDir(CommandContext context) {
        return new File(context.getAppConfig().getConfigDir().getSecurityDir(), "amps");
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new AmpManager(context.getManageClient());
    }

}
