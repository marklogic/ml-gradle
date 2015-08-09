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
public class CreateOtherServersCommand extends AbstractResourceCommand {

    public CreateOtherServersCommand() {
        setExecuteSortOrder(SortOrderConstants.MANAGE_OTHER_SERVERS);
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
