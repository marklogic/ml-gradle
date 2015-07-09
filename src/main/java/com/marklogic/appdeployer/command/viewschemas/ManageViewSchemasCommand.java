package com.marklogic.appdeployer.command.viewschemas;

import java.io.File;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.rest.mgmt.PayloadParser;
import com.marklogic.rest.mgmt.ResourceManager;
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
    protected String saveResource(ResourceManager mgr, CommandContext context, File f) {
        String payload = super.saveResource(mgr, context, f);
        if (payload != null) {
            PayloadParser parser = new PayloadParser();
            String viewSchemaName = parser.getPayloadFieldValue(payload, "view-schema-name");
            File viewDir = new File(f.getParentFile(), viewSchemaName + "-views");
            if (viewDir.exists()) {
                logger.info("VIEW DIR EXISTS!");
            }
        }
        return payload;
    }

}
