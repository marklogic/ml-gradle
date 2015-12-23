package com.marklogic.appdeployer.command.security;

import java.io.File;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.ResourceManager;
import com.marklogic.mgmt.security.ProtectedCollectionsManager;

public class DeployProtectedCollectionsCommand extends AbstractResourceCommand {

    public DeployProtectedCollectionsCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_PROTECTED_COLLECTIONS);
        setUndoSortOrder(SortOrderConstants.DELETE_PROTECTED_COLLECTIONS);
    }

    @Override
    protected File[] getResourceDirs(CommandContext context) {
        return new File[] { new File(context.getAppConfig().getConfigDir().getSecurityDir(), "protected-collections") };
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new ProtectedCollectionsManager(context.getManageClient());
    }

}
