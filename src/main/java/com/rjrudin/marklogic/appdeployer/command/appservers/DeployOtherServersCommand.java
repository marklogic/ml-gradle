package com.rjrudin.marklogic.appdeployer.command.appservers;

import java.io.File;

import com.rjrudin.marklogic.appdeployer.command.AbstractResourceCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.appservers.ServerManager;

/**
 * "Other" = non-REST-API servers. This will process every JSON/XML file that's not named "rest-api-server.*" in the
 * servers directory.
 */
public class DeployOtherServersCommand extends AbstractResourceCommand {

    public DeployOtherServersCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_OTHER_SERVERS);
        setUndoSortOrder(SortOrderConstants.DELETE_OTHER_SERVERS);
        setRestartAfterDelete(true);
    }

    @Override
    protected File[] getResourceDirs(CommandContext context) {
        return new File[] { new File(context.getAppConfig().getConfigDir().getBaseDir(), "servers") };
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new ServerManager(context.getManageClient(), context.getAppConfig().getGroupName());
    }

    @Override
    protected boolean isResourceFile(File f) {
        return super.isResourceFile(f) && !f.getName().startsWith("rest-api-server");
    }

    @Override
    public Integer getUndoSortOrder() {
        return 0;
    }

}
