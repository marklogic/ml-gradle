package com.marklogic.appdeployer.command.viewschemas;

import java.io.File;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.ResourceManager;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.mgmt.viewschemas.ViewManager;
import com.marklogic.mgmt.viewschemas.ViewSchemaManager;

/**
 * Processes each file in the view-schemas directory. For each one, then checks for a (view schema name)-views
 * directory in the view-schemas directory. If it exists, each file in that directory is processed as a view.
 *
 * This command defaults to storing view schemas and views in the schemas database associated with the default
 * content database. This can be overridden by setting the "databaseIdOrName" property. Unfortunately, this does
 * not yet allow for multiple databases to have view schemas. But you can achieve that by using multiple instances
 * of this class, each with a different view schemas path, which can be set via setViewSchemasPath.
 */
public class DeployViewSchemasCommand extends AbstractResourceCommand {

	private String databaseIdOrName;
	private String viewSchemasPath = "view-schemas";

    public DeployViewSchemasCommand() {
        // Don't need to delete anything, as view-schemas all live in a database
        setDeleteResourcesOnUndo(false);
        setExecuteSortOrder(SortOrderConstants.DEPLOY_SQL_VIEWS);
    }

    @Override
    protected File[] getResourceDirs(CommandContext context) {
        return new File[] { new File(context.getAppConfig().getConfigDir().getBaseDir(), viewSchemasPath) };
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
		String dbName = databaseIdOrName != null ? databaseIdOrName : context.getAppConfig().getContentDatabaseName();
        return new ViewSchemaManager(context.getManageClient(), dbName);
    }

    @Override
    protected void afterResourceSaved(ResourceManager mgr, CommandContext context, File resourceFile,
            SaveReceipt receipt) {
        PayloadParser parser = new PayloadParser();
        String viewSchemaName = parser.getPayloadFieldValue(receipt.getPayload(), "view-schema-name");
        File viewDir = new File(resourceFile.getParentFile(), viewSchemaName + "-views");
        if (viewDir.exists()) {
			String dbName = databaseIdOrName != null ? databaseIdOrName : context.getAppConfig().getContentDatabaseName();
            ViewManager viewMgr = new ViewManager(context.getManageClient(), dbName, viewSchemaName);
            for (File viewFile : listFilesInDirectory(viewDir)) {
                saveResource(viewMgr, context, viewFile);
            }
        }
    }

	public void setDatabaseIdOrName(String databaseIdOrName) {
		this.databaseIdOrName = databaseIdOrName;
	}

	public void setViewSchemasPath(String viewSchemasPath) {
		this.viewSchemasPath = viewSchemasPath;
	}
}
