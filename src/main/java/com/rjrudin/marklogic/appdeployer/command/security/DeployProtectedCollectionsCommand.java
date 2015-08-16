package com.rjrudin.marklogic.appdeployer.command.security;

import java.io.File;

import com.rjrudin.marklogic.appdeployer.command.AbstractResourceCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.security.ProtectedCollectionsManager;

public class DeployProtectedCollectionsCommand extends AbstractResourceCommand {

    public DeployProtectedCollectionsCommand() {
        setExecuteSortOrder(SortOrderConstants.CREATE_PROTECTED_COLLECTIONS);
        setUndoSortOrder(SortOrderConstants.DELETE_PROTECTED_COLLECTIONS);
    }

    @Override
    protected File getResourcesDir(CommandContext context) {
        return new File(context.getAppConfig().getConfigDir().getSecurityDir(), "protected-collections");
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new ProtectedCollectionsManager(context.getManageClient());
    }

}
