/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.admin;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.mgmt.admin.AdminConfig;
import com.marklogic.mgmt.admin.AdminManager;

/**
 * TODO Convert this into a real test, adding a isSslFipsEnabled method to AdminManager.
 */
public class SetSslFipsEnabledDebug {

    public static void main(String[] args) {
        ManageConfig config = new ManageConfig("localhost", 8002, "admin", "admin");
        ManageClient manageClient = new ManageClient(config);
        AppConfig appConfig = new AppConfig();
        AdminManager adminManager = new AdminManager(new AdminConfig("localhost", "admin"));
        CommandContext context = new CommandContext(appConfig, manageClient, adminManager);

        SetSslFipsEnabledCommand command = new SetSslFipsEnabledCommand(false);
        command.execute(context);
    }
}
