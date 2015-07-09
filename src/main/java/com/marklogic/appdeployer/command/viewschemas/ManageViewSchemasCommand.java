package com.marklogic.appdeployer.command.viewschemas;

import java.io.File;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.rest.mgmt.PayloadParser;
import com.marklogic.rest.mgmt.ResourceManager;
import com.marklogic.rest.mgmt.viewschemas.ViewManager;
import com.marklogic.rest.mgmt.viewschemas.ViewSchemaManager;

/**
 * So for a given view-schema resource, after it's been processed, we'll check for a (name)-views folder in the
 * view-schemas folder. If it exists, we'll process each file in the directory. We don't need to do anything on undeploy
 * fortunately.
 */
public class ManageViewSchemasCommand extends AbstractResourceCommand {

    public ManageViewSchemasCommand() {
        // Don't need to delete anything, as view-schemas all live in a database
        setDeleteResourcesOnUndo(false);
        setExecuteSortOrder(SortOrderConstants.CREATE_SQL_VIEWS);
    }

    @Override
    protected File getResourcesDir(CommandContext context) {
        return new File(context.getAppConfig().getConfigDir().getBaseDir(), "view-schemas");
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new ViewSchemaManager(context.getManageClient(), context.getAppConfig().getContentDatabaseName());
    }

    @Override
    protected void afterResourceSaved(ResourceManager mgr, CommandContext context, File resourceFile, String payload) {
        PayloadParser parser = new PayloadParser();
        String viewSchemaName = parser.getPayloadFieldValue(payload, "view-schema-name");
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
