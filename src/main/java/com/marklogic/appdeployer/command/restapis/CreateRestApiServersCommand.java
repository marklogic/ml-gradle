package com.marklogic.appdeployer.command.restapis;

import java.io.File;

import org.springframework.http.HttpMethod;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.CommandContext;
import com.marklogic.appdeployer.UndoableCommand;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.rest.mgmt.ManageClient;
import com.marklogic.rest.mgmt.admin.ActionRequiringRestart;
import com.marklogic.rest.mgmt.appservers.ServerManager;
import com.marklogic.rest.mgmt.restapis.RestApiManager;

public class CreateRestApiServersCommand extends AbstractCommand implements UndoableCommand {

    private boolean includeModules = true;
    private boolean includeContent = true;

    @Override
    public Integer getExecuteSortOrder() {
        return SortOrderConstants.CREATE_REST_API_SERVERS_ORDER;
    }

    @Override
    public Integer getUndoSortOrder() {
        return getExecuteSortOrder();
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

        // If we have a test REST API, first modify it to point at Documents for the modules database so we can safely
        // delete each REST API
        if (appConfig.isTestPortSet()) {
            ServerManager mgr = new ServerManager(manageClient);
            mgr.setModulesDatabaseToDocuments(appConfig.getTestRestServerName(), appConfig.getGroupName());
            context.getAdminManager().invokeActionRequiringRestart(new ActionRequiringRestart() {
                @Override
                public boolean execute() {
                    return deleteRestApi(appConfig.getTestRestServerName(), manageClient, false, true);
                }
            });

        }

        context.getAdminManager().invokeActionRequiringRestart(new ActionRequiringRestart() {
            @Override
            public boolean execute() {
                return deleteRestApi(appConfig.getRestServerName(), manageClient, includeModules, includeContent);
            }
        });
    }

    protected String getDefaultRestApiPayload() {
        return "{\"rest-api\": {\"name\":\"%%NAME%%\", \"group\":\"%%GROUP%%\", \"database\":\"%%DATABASE%%\", "
                + "\"modules-database\":\"%%MODULES-DATABASE%%\", \"port\":\"%%PORT%%\", \"xdbc-enabled\":true, "
                + "\"forests-per-host\":3, \"error-format\":\"json\"}}";
    }

    protected boolean deleteRestApi(String serverName, ManageClient manageClient, boolean includeModules,
            boolean includeContent) {
        if (new ServerManager(manageClient).serverExists(serverName)) {
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

    public boolean isIncludeModules() {
        return includeModules;
    }

    public void setIncludeModules(boolean includesModules) {
        this.includeModules = includesModules;
    }

    public boolean isIncludeContent() {
        return includeContent;
    }

    public void setIncludeContent(boolean includeContent) {
        this.includeContent = includeContent;
    }

}
