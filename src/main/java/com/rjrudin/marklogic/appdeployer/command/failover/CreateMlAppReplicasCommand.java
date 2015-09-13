package com.rjrudin.marklogic.appdeployer.command.failover;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rjrudin.marklogic.appdeployer.command.AbstractCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.mgmt.forests.ForestManager;
import com.rjrudin.marklogic.mgmt.hosts.HostManager;

public class CreateMlAppReplicasCommand extends AbstractCommand {

    private int securityReplicasPerHost = 1;
    private int schemasReplicasPerHost = 1;
    private int appServicesReplicasPerHost = 1;
    private int metersReplicasPerHost = 1;

    public CreateMlAppReplicasCommand() {
        // As this task has no dependencies, it should be safe to execute it first
        setExecuteSortOrder(0);
    }

    @Override
    public void execute(CommandContext context) {
        HostManager hostMgr = new HostManager(context.getManageClient());
        ForestManager forestMgr = new ForestManager(context.getManageClient());

        List<String> hostIds = hostMgr.getHostIds();

        logger.info("All host ids: " + hostIds);

        // Find the host that has the default security/schemas/app-services/meters forests on it
        String primaryForestName = "Security";
        String primaryForestHostId = forestMgr.getHostId(primaryForestName);
        logger.info(primaryForestName + " host: " + primaryForestHostId);

        // Now for each other host, create N replicas on the host
        int resourceCounter = 2;
        Map<String, String> replicaNamesAndHostIds = new HashMap<>();
        for (String hostId : hostIds) {
            if (!hostId.equals(primaryForestHostId)) {
                String name = primaryForestName + "-" + resourceCounter;
                if (forestMgr.exists(name)) {
                    logger.info(format("Replica forest %s already exists, so not creating", name));
                } else {
                    forestMgr.createForestWithName(name, hostId);
                }
                replicaNamesAndHostIds.put(name, hostId);
                resourceCounter++;
            }
        }

        if (!replicaNamesAndHostIds.isEmpty()) {
            forestMgr.setReplicas(primaryForestName, replicaNamesAndHostIds);
        }
    }

    public void setSecurityReplicasPerHost(int securityReplicasPerHost) {
        this.securityReplicasPerHost = securityReplicasPerHost;
    }

    public void setSchemasReplicasPerHost(int schemasReplicasPerHost) {
        this.schemasReplicasPerHost = schemasReplicasPerHost;
    }

    public void setAppServicesReplicasPerHost(int appServicesReplicasPerHost) {
        this.appServicesReplicasPerHost = appServicesReplicasPerHost;
    }

    public void setMetersReplicasPerHost(int metersReplicasPerHost) {
        this.metersReplicasPerHost = metersReplicasPerHost;
    }
}
