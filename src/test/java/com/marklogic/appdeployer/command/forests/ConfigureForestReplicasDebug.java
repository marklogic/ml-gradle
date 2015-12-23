package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;

/**
 * Not an actual test, as this depends on an environment with multiple hosts, which is normally not the case on a
 * development machine.
 */
public class ConfigureForestReplicasDebug {

    public static void main(String[] args) {
        final String host = args[0];
        final String password = args[1];

        ManageConfig config = new ManageConfig(host, 8002, "admin", password);
        ManageClient manageClient = new ManageClient(config);
        AppConfig appConfig = new AppConfig();
        CommandContext context = new CommandContext(appConfig, manageClient, null);

        // Configure replicas
        ConfigureForestReplicasCommand command = new ConfigureForestReplicasCommand();
        // command.getForestNamesAndReplicaCounts().put("Security", 1);
        // command.getForestNamesAndReplicaCounts().put("Schemas", 2);
        //command.getDatabaseNamesAndReplicaCounts().put("failover-example-content", 2);
        command.execute(context);

        // And then delete those replicas
        command.undo(context);
    }
}
