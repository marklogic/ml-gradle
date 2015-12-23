package com.marklogic.appdeployer.command;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.admin.AdminManager;

public class CommandContext {

    private AppConfig appConfig;
    private ManageClient manageClient;
    private AdminManager adminManager;

    public CommandContext(AppConfig appConfig, ManageClient manageClient, AdminManager adminManager) {
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
