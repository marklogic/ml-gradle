package com.marklogic.appdeployer.command.appservers;

import java.io.File;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.rest.mgmt.ResourceManager;
import com.marklogic.rest.mgmt.appservers.ServerManager;

/**
 * "Other" = non-REST-API servers. This will process every JSON/XML file that's not named "rest-api-server.*" in the
 * servers directory.
 */
public class ManageOtherServersCommand extends AbstractResourceCommand {

    public ManageOtherServersCommand() {
        setExecuteSortOrder(SortOrderConstants.MANAGE_OTHER_SERVERS_ORDER);
        setRestartAfterDelete(true);
    }

    @Override
    protected File getResourcesDir(CommandContext context) {
        return new File(context.getAppConfig().getConfigDir().getBaseDir(), "servers");
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new ServerManager(context.getManageClient(), context.getAppConfig().getGroupName());
    }

    @Override
    protected boolean isResourceFile(File f) {
        return super.isResourceFile(f) && !f.getName().startsWith("rest-api-server");
    }

    /**
     * Nothing should depend on the existence of an ODBC server, so we can delete it right away.
     */
    @Override
    public Integer getUndoSortOrder() {
        return 0;
    }

}
