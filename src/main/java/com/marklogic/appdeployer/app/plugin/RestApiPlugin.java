package com.marklogic.appdeployer.app.plugin;

import java.io.File;

import org.springframework.http.HttpMethod;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.app.AbstractPlugin;
import com.marklogic.appdeployer.app.ConfigDir;
import com.marklogic.appdeployer.app.RequirestRestartOnDelete;
import com.marklogic.appdeployer.mgmt.ManageClient;
import com.marklogic.appdeployer.mgmt.services.ServiceManager;

public class RestApiPlugin extends AbstractPlugin implements RequirestRestartOnDelete {

    private boolean includeModules = true;
    private boolean includeContent = true;

    @Override
    public Integer getSortOrderOnCreate() {
        return 100;
    }

    @Override
    public void onCreate(AppConfig appConfig, ConfigDir configDir, ManageClient manageClient) {
        File f = configDir.getRestApiFile();
        String input = copyFileToString(f);

        ServiceManager mgr = new ServiceManager(manageClient);

        mgr.createRestApi(appConfig.getRestServerName(), replaceConfigTokens(input, appConfig, false));

        if (appConfig.isTestPortSet()) {
            mgr.createRestApi(appConfig.getTestRestServerName(), replaceConfigTokens(input, appConfig, true));
        }
    }

    @Override
    public void onDelete(AppConfig appConfig, ConfigDir configDir, ManageClient manageClient) {
        String path = manageClient.getBaseUrl() + "/v1/rest-apis/" + appConfig.getName() + "?";
        if (includeModules) {
            path += "include=modules&";
        }
        if (includeContent) {
            path += "include=content";
        }
        logger.info("Deleting REST API, path: " + path);
        manageClient.getRestTemplate().exchange(path, HttpMethod.DELETE, null, String.class);
        logger.info("Deleted REST API");
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
