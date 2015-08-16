package com.rjrudin.marklogic.appdeployer.command.groups;

import java.io.File;

import com.rjrudin.marklogic.appdeployer.command.AbstractResourceCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.groups.GroupManager;

public class DeployGroupsCommand extends AbstractResourceCommand {

    public DeployGroupsCommand() {
        setExecuteSortOrder(SortOrderConstants.CREATE_GROUPS);
        setUndoSortOrder(SortOrderConstants.DELETE_GROUPS);
    }

    @Override
    protected File getResourcesDir(CommandContext context) {
        return new File(context.getAppConfig().getConfigDir().getBaseDir(), "groups");
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new GroupManager(context.getManageClient());
    }

}
