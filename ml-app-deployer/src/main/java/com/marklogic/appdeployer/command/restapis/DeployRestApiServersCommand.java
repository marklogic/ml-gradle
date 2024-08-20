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
package com.marklogic.appdeployer.command.restapis;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.appdeployer.command.UndoableCommand;
import com.marklogic.appdeployer.util.RestApiUtil;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.admin.ActionRequiringRestart;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import com.marklogic.mgmt.resource.restapis.RestApiDeletionRequest;
import com.marklogic.mgmt.resource.restapis.RestApiManager;

import java.io.File;

/**
 * By default, when this command deletes a REST API server, it will delete the modules database but not the content
 * database. The content database is expected to be deleted by an instance of DeployContentDatabasesCommand. If you're
 * not using that command, just set deleteContentDatabase to true.
 */
public class DeployRestApiServersCommand extends AbstractCommand implements UndoableCommand {

	private boolean deleteModulesDatabase = true;
	private boolean deleteContentDatabase = false;

	// Controls whether this command first deletes replica forests if it's supposed to delete the modules or content databases
	private boolean deleteModulesReplicaForests = true;
	private boolean deleteContentReplicaForests = true;

	private String restApiFilename;

	public DeployRestApiServersCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_REST_API_SERVERS);
	}

	public DeployRestApiServersCommand(String restApiFilename) {
		this();
		this.restApiFilename = restApiFilename;
	}

	public DeployRestApiServersCommand(boolean deleteContentDatabase) {
		this();
		this.deleteContentDatabase = deleteContentDatabase;
	}

	public DeployRestApiServersCommand(String restApiFilename, boolean deleteContentDatabase) {
		this();
		this.restApiFilename = restApiFilename;
		this.deleteContentDatabase = deleteContentDatabase;
	}

	@Override
	public Integer getUndoSortOrder() {
		return SortOrderConstants.DELETE_REST_API_SERVERS;
	}

	@Override
	public void execute(CommandContext context) {
		String payload = getRestApiPayload(context);
		if (payload != null) {
			AppConfig appConfig = context.getAppConfig();
			RestApiManager mgr = new RestApiManager(context.getManageClient(), appConfig.getGroupName());

			mgr.createRestApi(payloadTokenReplacer.replaceTokens(payload, appConfig, false));

			if (appConfig.isTestPortSet()) {
				mgr.createRestApi(payloadTokenReplacer.replaceTokens(payload, appConfig, true));
			}
		}
	}

	protected String getRestApiPayload(CommandContext context) {
		File f = findRestApiConfigFile(context);
		if (f != null && f.exists()) {
			return copyFileToString(f);
		} else if (context.getAppConfig().isNoRestServer()) {
			logger.info(format("Could not find REST API file at %s, will not deploy/undeploy a REST API server", f.getAbsolutePath()));
			return null;
		} else {
			logger.info(format("Could not find REST API file at %s, will use default payload", f.getAbsolutePath()));
			return getDefaultRestApiPayload(context);
		}
	}

	/**
	 * Will always return a non-null file; if it can't find a file, returns a reference to the first file it tried.
	 *
	 * @param context
	 * @return
	 */
	protected File findRestApiConfigFile(CommandContext context) {
		if (restApiFilename != null) {
			File restApiFile = null;
			// Check each ConfigDir for the file, with the last one winning
			for (ConfigDir configDir : context.getAppConfig().getConfigDirs()) {
				File f = new File(configDir.getBaseDir(), restApiFilename);
				if (f.exists()) {
					restApiFile = f;
				}
			}
			if (restApiFile == null) {
				restApiFile = new File(context.getAppConfig().getFirstConfigDir().getBaseDir(), restApiFilename);
			}
			return restApiFile;
		} else {
			File restApiFile = null;
			// Check each ConfigDir for the file, with the last one winning
			for (ConfigDir configDir : context.getAppConfig().getConfigDirs()) {
				File f = configDir.getRestApiFile();
				if (f != null && f.exists()) {
					restApiFile = f;
				}
			}
			if (restApiFile == null) {
				restApiFile = context.getAppConfig().getFirstConfigDir().getRestApiFile();
			}
			return restApiFile;
		}
	}

	@Override
	public void undo(CommandContext context) {
		deleteTestRestServer(context);
		deleteMainRestServer(context);
	}

	/**
	 * If we have a test REST API, we first modify it to point at Documents for the modules database so we can safely
	 * delete each REST API.
	 *
	 * @param context
	 */
	protected void deleteTestRestServer(CommandContext context) {
		final AppConfig appConfig = context.getAppConfig();
		final ManageClient manageClient = context.getManageClient();

		ServerManager mgr = new ServerManager(manageClient, appConfig.getGroupName());
		if (appConfig.isTestPortSet() && mgr.exists(appConfig.getTestRestServerName())) {
			mgr.setModulesDatabaseToDocuments(appConfig.getTestRestServerName());
			context.getAdminManager().invokeActionRequiringRestart(new ActionRequiringRestart() {
				@Override
				public boolean execute() {
					return deleteRestApi(appConfig.getTestRestServerName(), appConfig.getGroupName(), manageClient,
						false, true);
				}
			});
		}
	}

	protected void deleteMainRestServer(CommandContext context) {
		final AppConfig appConfig = context.getAppConfig();
		final ManageClient manageClient = context.getManageClient();

		ServerManager mgr = new ServerManager(manageClient, appConfig.getGroupName());

		String payload = getRestApiPayload(context);
		if (payload != null) {
			payload = payloadTokenReplacer.replaceTokens(payload, appConfig, false);
			final String serverName = new RestApiManager(manageClient).extractNameFromJson(payload);

			if (mgr.exists(serverName)) {
				context.getAdminManager().invokeActionRequiringRestart(new ActionRequiringRestart() {
					@Override
					public boolean execute() {
						return deleteRestApi(serverName, appConfig.getGroupName(), manageClient, deleteModulesDatabase,
							deleteContentDatabase);
					}
				});
			}
		}
	}

	protected String getDefaultRestApiPayload(CommandContext context) {
		// Use contentForestsPerHost in case the user does not have a database file for the content database that would
		// otherwise control the number of forests created (as it would be created before the REST API instance is
		// created)
		Integer count = context.getAppConfig().getContentForestsPerHost();
		return count != null ? RestApiUtil.buildDefaultRestApiJson(count) : RestApiUtil.buildDefaultRestApiJson();
	}

	/**
	 * Delete the REST API server with the given name.
	 *
	 * @param serverName
	 * @param groupName
	 * @param manageClient
	 * @param includeModules
	 * @param includeContent
	 * @return
	 */
	protected boolean deleteRestApi(String serverName, String groupName, ManageClient manageClient,
	                                boolean includeModules, boolean includeContent) {
		RestApiDeletionRequest request = new RestApiDeletionRequest(serverName, groupName);
		request.setIncludeContent(includeContent);
		request.setIncludeModules(includeModules);
		request.setDeleteContentReplicaForests(isDeleteContentReplicaForests());
		request.setDeleteModulesReplicaForests(isDeleteModulesReplicaForests());
		return new RestApiManager(manageClient, groupName).deleteRestApi(request);
	}

	public boolean isDeleteModulesDatabase() {
		return deleteModulesDatabase;
	}

	public void setDeleteModulesDatabase(boolean includesModules) {
		this.deleteModulesDatabase = includesModules;
	}

	public boolean isDeleteContentDatabase() {
		return deleteContentDatabase;
	}

	public void setDeleteContentDatabase(boolean includeContent) {
		this.deleteContentDatabase = includeContent;
	}

	public String getRestApiFilename() {
		return restApiFilename;
	}

	public void setRestApiFilename(String restApiFilename) {
		this.restApiFilename = restApiFilename;
	}

	public boolean isDeleteModulesReplicaForests() {
		return deleteModulesReplicaForests;
	}

	public void setDeleteModulesReplicaForests(boolean deleteModulesReplicaForests) {
		this.deleteModulesReplicaForests = deleteModulesReplicaForests;
	}

	public boolean isDeleteContentReplicaForests() {
		return deleteContentReplicaForests;
	}

	public void setDeleteContentReplicaForests(boolean deleteContentReplicaForests) {
		this.deleteContentReplicaForests = deleteContentReplicaForests;
	}
}
