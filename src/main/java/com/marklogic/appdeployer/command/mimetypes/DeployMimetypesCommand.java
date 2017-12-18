package com.marklogic.appdeployer.command.mimetypes;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.mgmt.resource.mimetypes.MimetypeManager;

import java.io.File;

public class DeployMimetypesCommand extends AbstractResourceCommand {

    public DeployMimetypesCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_MIMETYPES);
        setUndoSortOrder(SortOrderConstants.DELETE_MIMETYPES);
        setRestartAfterDelete(true);
    }

    @Override
    protected File[] getResourceDirs(CommandContext context) {
    	return findResourceDirs(context, configDir -> configDir.getMimetypesDir());
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new MimetypeManager(context.getManageClient());
    }

    /**
     * As of ML 8.0-4, any time a mimetype is created or updated, ML must be restarted.
     */
    @Override
    protected void afterResourceSaved(ResourceManager mgr, CommandContext context, File resourceFile,
            SaveReceipt receipt) {
        logger.info("Waiting for restart after saving mimetype");
        context.getAdminManager().waitForRestart();
    }

}
