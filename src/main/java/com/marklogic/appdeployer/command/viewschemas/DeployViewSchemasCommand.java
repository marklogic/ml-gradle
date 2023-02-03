/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.appdeployer.command.viewschemas;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.ResourceReference;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.mgmt.resource.viewschemas.ViewManager;
import com.marklogic.mgmt.resource.viewschemas.ViewSchemaManager;

import java.io.File;

/**
 * Processes each file in the view-schemas directory. For each one, then checks for a (view schema name)-views
 * directory in the view-schemas directory. If it exists, each file in that directory is processed as a view.
 * <p>
 * As of version 2.9.0, this command supports deploying view schemas to any database. Note though that the view schemas
 * aren't deployed into the targeted database, but rather the schemas database associated with the target database.
 */
public class DeployViewSchemasCommand extends AbstractResourceCommand {

	private String currentDatabaseIdOrName;
	private ViewSchemaManager currentViewSchemaManager;

	public DeployViewSchemasCommand() {
		// Don't need to delete anything, as view-schemas all live in a database
		setDeleteResourcesOnUndo(false);
		setExecuteSortOrder(SortOrderConstants.DEPLOY_SQL_VIEWS);
	}

	@Override
	public void execute(CommandContext context) {
		AppConfig appConfig = context.getAppConfig();
		for (ConfigDir configDir : appConfig.getConfigDirs()) {
			deployViewSchemas(context, configDir, appConfig.getContentDatabaseName());
			for (File dir : configDir.getDatabaseResourceDirectories()) {
				String databaseName = determineDatabaseNameForDatabaseResourceDirectory(context, configDir, dir);
				if (databaseName != null) {
					deployViewSchemas(context, new ConfigDir(dir), databaseName);
				}
			}
		}
	}

	protected void deployViewSchemas(CommandContext context, ConfigDir configDir, String databaseIdOrName) {
		currentDatabaseIdOrName = databaseIdOrName;
		currentViewSchemaManager = new ViewSchemaManager(context.getManageClient(), databaseIdOrName);
		processExecuteOnResourceDir(context, configDir.getViewSchemasDir());
	}

	/**
	 * Not used since we override the execute method of the parent class.
	 *
	 * @param context
	 * @return
	 */
	@Override
	protected File[] getResourceDirs(CommandContext context) {
		return null;
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return currentViewSchemaManager;
	}

	@Override
	protected void afterResourceSaved(ResourceManager mgr, CommandContext context, ResourceReference reference, SaveReceipt receipt) {
		if (reference != null) {
			File resourceFile = reference.getLastFile();
			PayloadParser parser = new PayloadParser();
			String viewSchemaName = parser.getPayloadFieldValue(receipt.getPayload(), "view-schema-name");
			File viewDir = new File(resourceFile.getParentFile(), viewSchemaName + "-views");
			if (viewDir.exists()) {
				ViewManager viewMgr = new ViewManager(context.getManageClient(), currentDatabaseIdOrName, viewSchemaName);
				for (File viewFile : listFilesInDirectory(viewDir)) {
					saveResource(viewMgr, context, viewFile);
				}
			}
		} else {
			logger.info("No ResourceReference provided in afterResourceSaved, so unable to create views associated with view schema");
		}
	}
}
