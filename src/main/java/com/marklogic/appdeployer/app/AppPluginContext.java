package com.marklogic.appdeployer.app;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.rest.mgmt.ManageClient;
import com.marklogic.rest.mgmt.admin.AdminManager;

public class AppPluginContext {

    private AppConfig appConfig;
    private ConfigDir configDir;
    private ManageClient manageClient;
    private AdminManager adminManager;

    public AppPluginContext(AppConfig appConfig, ConfigDir configDir, ManageClient manageClient,
            AdminManager adminManager) {
        super();
        this.appConfig = appConfig;
        this.configDir = configDir;
        this.manageClient = manageClient;
        this.adminManager = adminManager;
    }

    public AppConfig getAppConfig() {
        return appConfig;
    }

    public ConfigDir getConfigDir() {
        return configDir;
    }

    public ManageClient getManageClient() {
        return manageClient;
    }

    public AdminManager getAdminManager() {
        return adminManager;
    }
}
