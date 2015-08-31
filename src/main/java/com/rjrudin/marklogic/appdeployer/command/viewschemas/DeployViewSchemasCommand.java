package com.rjrudin.marklogic.appdeployer.command.viewschemas;

import java.io.File;

import com.rjrudin.marklogic.appdeployer.command.AbstractResourceCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.mgmt.PayloadParser;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.SaveReceipt;
import com.rjrudin.marklogic.mgmt.viewschemas.ViewManager;
import com.rjrudin.marklogic.mgmt.viewschemas.ViewSchemaManager;

/**
 * So for a given view-schema resource, after it's been processed, we'll check for a (name)-views folder in the
 * view-schemas folder. If it exists, we'll process each file in the directory. We don't need to do anything on undeploy
 * fortunately.
 */
public class DeployViewSchemasCommand extends AbstractResourceCommand {

    public DeployViewSchemasCommand() {
        // Don't need to delete anything, as view-schemas all live in a database
        setDeleteResourcesOnUndo(false);
        setExecuteSortOrder(SortOrderConstants.DEPLOY_SQL_VIEWS);
    }

    @Override
    protected File[] getResourceDirs(CommandContext context) {
        return new File[] { new File(context.getAppConfig().getConfigDir().getBaseDir(), "view-schemas") };
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new ViewSchemaManager(context.getManageClient(), context.getAppConfig().getContentDatabaseName());
    }

    @Override
    protected void afterResourceSaved(ResourceManager mgr, CommandContext context, File resourceFile,
            SaveReceipt receipt) {
        PayloadParser parser = new PayloadParser();
        String viewSchemaName = parser.getPayloadFieldValue(receipt.getPayload(), "view-schema-name");
        File viewDir = new File(resourceFile.getParentFile(), viewSchemaName + "-views");
        if (viewDir.exists()) {
            ViewManager viewMgr = new ViewManager(context.getManageClient(), context.getAppConfig()
                    .getContentDatabaseName(), viewSchemaName);
            for (File viewFile : viewDir.listFiles()) {
                if (isResourceFile(viewFile)) {
                    saveResource(viewMgr, context, viewFile);
                }
            }
        }
    }

}
