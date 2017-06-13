package com.marklogic.appdeployer.command.restapis;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.appdeployer.command.UndoableCommand;
import com.marklogic.appdeployer.util.RestApiUtil;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.admin.ActionRequiringRestart;
import com.marklogic.mgmt.appservers.ServerManager;
import com.marklogic.mgmt.restapis.RestApiManager;

import java.io.File;

/**
 * By default, when this command deletes a REST API server, it will delete the modules database but not the content
 * database. The content database is expected to be deleted by an instance of DeployContentDatabasesCommand. If you're
 * not using that command, just set deleteContentDatabase to true.
 */
public class DeployRestApiServersCommand extends AbstractCommand implements UndoableCommand {

	private boolean deleteModulesDatabase = true;
	private boolean deleteContentDatabase = false;

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
			RestApiManager mgr = new RestApiManager(context.getManageClient());
			AppConfig appConfig = context.getAppConfig();

			mgr.createRestApi(payloadTokenReplacer.replaceTokens(payload, appConfig, false));

			if (appConfig.isTestPortSet()) {
				mgr.createRestApi(payloadTokenReplacer.replaceTokens(payload, appConfig, true));
			}
		}
	}

	protected String getRestApiPayload(CommandContext context) {
		File f = findRestApiConfigFile(context);
		if (f.exists()) {
			return copyFileToString(f);
		} else if (context.getAppConfig().isNoRestServer()) {
			logger.info(format("Could not find REST API file at %s, will not deploy/undeploy a REST API server", f.getAbsolutePath()));
			return null;
		} else {
			logger.info(format("Could not find REST API file at %s, will use default payload", f.getAbsolutePath()));
			return getDefaultRestApiPayload();
		}
	}

	protected File findRestApiConfigFile(CommandContext context) {
		if (restApiFilename != null) {
			return new File(context.getAppConfig().getConfigDir().getBaseDir(), restApiFilename);
		} else {
			return context.getAppConfig().getConfigDir().getRestApiFile();
		}
	}

	@Override
	public void undo(CommandContext context) {
		deleteTestRestServer(context);
		deleteMainRestServer(context);
	}

	/**
	 * If we have a test REST API, we first modify it to point at Documents for the modules database so we can safely
	 * delete each REST API
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

	protected String getDefaultRestApiPayload() {
		return RestApiUtil.buildDefaultRestApiJson();
	}

	protected boolean deleteRestApi(String serverName, String groupName, ManageClient manageClient,
									boolean includeModules, boolean includeContent) {
		return new RestApiManager(manageClient).deleteRestApi(serverName, groupName, includeModules, includeContent);
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
}
