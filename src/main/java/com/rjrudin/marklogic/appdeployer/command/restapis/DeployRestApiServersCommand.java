package com.rjrudin.marklogic.appdeployer.command.restapis;

import java.io.File;

import org.springframework.http.HttpMethod;

import com.rjrudin.marklogic.appdeployer.AppConfig;
import com.rjrudin.marklogic.appdeployer.command.AbstractCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.appdeployer.command.UndoableCommand;
import com.rjrudin.marklogic.appdeployer.util.RestApiUtil;
import com.rjrudin.marklogic.mgmt.ManageClient;
import com.rjrudin.marklogic.mgmt.admin.ActionRequiringRestart;
import com.rjrudin.marklogic.mgmt.appservers.ServerManager;
import com.rjrudin.marklogic.mgmt.restapis.RestApiManager;

/**
 * By default, when this command deletes a REST API server, it will delete the modules database but not the content
 * database. The content database is expected to be deleted by an instance of CreateContentDatabasesCommand. If you're
 * not using that command, just set deleteContentDatabase to true.
 */
public class DeployRestApiServersCommand extends AbstractCommand implements UndoableCommand {

    private boolean deleteModulesDatabase = true;
    private boolean deleteContentDatabase = false;

    public DeployRestApiServersCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_REST_API_SERVERS);
    }

    @Override
    public Integer getUndoSortOrder() {
        return SortOrderConstants.DELETE_REST_API_SERVERS;
    }

    @Override
    public void execute(CommandContext context) {
        File f = context.getAppConfig().getConfigDir().getRestApiFile();
        String payload = null;
        if (f.exists()) {
            payload = copyFileToString(f);
        } else {
            logger.info(format("Could not find REST API file at %s, will use default payload", f.getAbsolutePath()));
            payload = getDefaultRestApiPayload();
        }

        RestApiManager mgr = new RestApiManager(context.getManageClient());
        AppConfig appConfig = context.getAppConfig();

        mgr.createRestApi(appConfig.getRestServerName(), tokenReplacer.replaceTokens(payload, appConfig, false));

        if (appConfig.isTestPortSet()) {
            mgr.createRestApi(appConfig.getTestRestServerName(), tokenReplacer.replaceTokens(payload, appConfig, true));
        }
    }

    @Override
    public void undo(CommandContext context) {
        final AppConfig appConfig = context.getAppConfig();
        final ManageClient manageClient = context.getManageClient();

        ServerManager mgr = new ServerManager(manageClient, appConfig.getGroupName());
        // If we have a test REST API, first modify it to point at Documents for the modules database so we can safely
        // delete each REST API
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

        if (mgr.exists(appConfig.getRestServerName())) {
            context.getAdminManager().invokeActionRequiringRestart(new ActionRequiringRestart() {
                @Override
                public boolean execute() {
                    return deleteRestApi(appConfig.getRestServerName(), appConfig.getGroupName(), manageClient,
                            deleteModulesDatabase, deleteContentDatabase);
                }
            });
        }
    }

    protected String getDefaultRestApiPayload() {
        return RestApiUtil.buildDefaultRestApiJson();
    }

    protected boolean deleteRestApi(String serverName, String groupName, ManageClient manageClient,
            boolean includeModules, boolean includeContent) {
        if (new ServerManager(manageClient, groupName).exists(serverName)) {
            String path = format("%s/v1/rest-apis/%s?", manageClient.getBaseUrl(), serverName);
            if (includeModules) {
                path += "include=modules&";
            }
            if (includeContent) {
                path += "include=content";
            }
            logger.info("Deleting REST API, path: " + path);
            manageClient.getRestTemplate().exchange(path, HttpMethod.DELETE, null, String.class);
            logger.info("Deleted REST API");
            return true;
        } else {
            logger.info(format("Server %s does not exist, not deleting", serverName));
            return false;
        }
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

}
