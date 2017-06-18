package com.marklogic.appdeployer.command.groups;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.mgmt.resource.groups.GroupManager;

import java.io.File;

public class DeployGroupsCommand extends AbstractResourceCommand {

    public DeployGroupsCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_GROUPS);
        setUndoSortOrder(SortOrderConstants.DELETE_GROUPS);
    }

    @Override
    protected File[] getResourceDirs(CommandContext context) {
        return new File[] { new File(context.getAppConfig().getConfigDir().getBaseDir(), "groups") };
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new GroupManager(context.getManageClient());
    }

    /**
     * Does a poor man's job of checking for a restart by checking for "cache-size" in the payload. This doesn't mean a
     * restart has occurred - the cache size may not changed - but that's fine, as the waitForRestart method on
     * AdminManager will quickly exit.
     */
    @Override
    protected void afterResourceSaved(ResourceManager mgr, CommandContext context, File resourceFile,
            SaveReceipt receipt) {
        String payload = receipt.getPayload();
        if (payload != null && payload.contains("cache-size") && context.getAdminManager() != null) {
            if (logger.isDebugEnabled()) {
                logger.info("Group payload contains cache-size parameter, so waiting for ML to restart");
            }
            context.getAdminManager().waitForRestart();
        }
    }
}
