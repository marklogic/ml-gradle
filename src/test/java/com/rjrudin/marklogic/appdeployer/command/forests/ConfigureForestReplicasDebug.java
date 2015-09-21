package com.rjrudin.marklogic.appdeployer.command.forests;

import com.rjrudin.marklogic.appdeployer.AppConfig;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.mgmt.ManageClient;
import com.rjrudin.marklogic.mgmt.ManageConfig;
import com.rjrudin.marklogic.mgmt.forests.ForestManager;

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

        ConfigureForestReplicasCommand command = new ConfigureForestReplicasCommand();
        command.getForestNamesAndReplicaCounts().put("Security", 1);
        command.getForestNamesAndReplicaCounts().put("Schemas", 2);
        command.execute(context);

        ForestManager mgr = new ForestManager(manageClient);
        mgr.deleteReplicas("Security");
        mgr.deleteReplicas("Schemas");
    }
}
