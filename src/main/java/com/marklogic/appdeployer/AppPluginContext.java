package com.marklogic.appdeployer;

import com.marklogic.rest.mgmt.ManageClient;
import com.marklogic.rest.mgmt.admin.AdminManager;

public class AppPluginContext {

    private AppConfig appConfig;
    private ManageClient manageClient;
    private AdminManager adminManager;

    public AppPluginContext(AppConfig appConfig, ManageClient manageClient, AdminManager adminManager) {
        super();
        this.appConfig = appConfig;
        this.manageClient = manageClient;
        this.adminManager = adminManager;
    }

    public AppConfig getAppConfig() {
        return appConfig;
    }

    public ManageClient getManageClient() {
        return manageClient;
    }

    public AdminManager getAdminManager() {
        return adminManager;
    }
}
