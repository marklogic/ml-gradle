package com.marklogic.appdeployer.command;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.admin.AdminManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the context within which a command executes, which includes:
 *
 * <ul>
 *     <li>An AppConfig object that defines configuration values for an application</li>
 *     <li>A ManageClient for connecting to the Manage API</li>
 *     <li>An AdminManager for performing operations against the Admin app server</li>
 *     <li>A context map that commands are free to store anything they wish within</li>
 * </ul>
 */
public class CommandContext {

    private AppConfig appConfig;
    private ManageClient manageClient;
    private AdminManager adminManager;

    private Map<String, Object> contextMap;

    public CommandContext(AppConfig appConfig, ManageClient manageClient, AdminManager adminManager) {
        super();
        this.appConfig = appConfig;
        this.manageClient = manageClient;
        this.adminManager = adminManager;
        this.contextMap = new HashMap<>();
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

	public Map<String, Object> getContextMap() {
		return contextMap;
	}

	public void setContextMap(Map<String, Object> contextMap) {
		this.contextMap = contextMap;
	}
}
