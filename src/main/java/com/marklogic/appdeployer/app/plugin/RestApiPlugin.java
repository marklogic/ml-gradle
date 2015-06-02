package com.marklogic.appdeployer.app.plugin;

import java.io.File;

import org.springframework.http.HttpMethod;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.app.AppPluginContext;
import com.marklogic.appdeployer.mgmt.ManageClient;
import com.marklogic.appdeployer.mgmt.admin.ActionRequiringRestart;
import com.marklogic.appdeployer.mgmt.appservers.ServerManager;
import com.marklogic.appdeployer.mgmt.services.ServiceManager;

public class RestApiPlugin extends AbstractPlugin {

    private boolean includeModules = true;
    private boolean includeContent = true;

    @Override
    public Integer getSortOrderOnCreate() {
        return 100;
    }

    @Override
    public void onCreate(AppPluginContext context) {
        File f = context.getConfigDir().getRestApiFile();
        String input = copyFileToString(f);

        ServiceManager mgr = new ServiceManager(context.getManageClient());
        AppConfig appConfig = context.getAppConfig();

        mgr.createRestApi(appConfig.getRestServerName(), replaceConfigTokens(input, appConfig, false));

        if (appConfig.isTestPortSet()) {
            mgr.createRestApi(appConfig.getTestRestServerName(), replaceConfigTokens(input, appConfig, true));
        }
    }

    @Override
    public void onDelete(AppPluginContext context) {
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
