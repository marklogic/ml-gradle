package com.rjrudin.marklogic.appdeployer.command.admin;

import com.rjrudin.marklogic.appdeployer.AppConfig;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.mgmt.ManageClient;
import com.rjrudin.marklogic.mgmt.ManageConfig;
import com.rjrudin.marklogic.mgmt.admin.AdminConfig;
import com.rjrudin.marklogic.mgmt.admin.AdminManager;

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
