package com.marklogic.appdeployer.command.triggers;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.ResourceManager;
import com.marklogic.mgmt.triggers.TriggerManager;

import java.io.File;

/**
 * Defaults to the triggers database name in the AppConfig instance. Can be overridden via the databaseNameOrId
 * property.
 */
public class DeployTriggersCommand extends AbstractResourceCommand {

    private String databaseIdOrName;

    public DeployTriggersCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_TRIGGERS);
        // Triggers are stored in a database, so we don't need to delete them as the database will be deleted
        setDeleteResourcesOnUndo(false);
    }

    @Override
    protected File[] getResourceDirs(CommandContext context) {
        return new File[] { new File(context.getAppConfig().getConfigDir().getBaseDir(), "triggers") };
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        String db = databaseIdOrName != null ? databaseIdOrName : context.getAppConfig().getTriggersDatabaseName();
        return new TriggerManager(context.getManageClient(), db);
    }

    public void setDatabaseIdOrName(String databaseIdOrName) {
        this.databaseIdOrName = databaseIdOrName;
    }

}
