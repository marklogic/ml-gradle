package com.rjrudin.marklogic.appdeployer.command;

import com.rjrudin.marklogic.appdeployer.AppConfig;
import com.rjrudin.marklogic.mgmt.ManageClient;
import com.rjrudin.marklogic.mgmt.admin.AdminManager;

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
