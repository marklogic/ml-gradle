package com.marklogic.appdeployer.command.appservers;

import java.io.File;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.ResourceFilenameFilter;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.ResourceManager;
import com.marklogic.mgmt.appservers.ServerManager;

/**
 * "Other" = non-REST-API servers. This will process every JSON/XML file that's not named "rest-api-server.*" in the
 * servers directory.
 */
public class DeployOtherServersCommand extends AbstractResourceCommand {

    public DeployOtherServersCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_OTHER_SERVERS);
        setUndoSortOrder(SortOrderConstants.DELETE_OTHER_SERVERS);
        setRestartAfterDelete(true);
        setResourceFilenameFilter(new ResourceFilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return super.accept(dir, name) && !name.startsWith("rest-api-server");
            }
        });
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
    public Integer getUndoSortOrder() {
        return 0;
    }

}
