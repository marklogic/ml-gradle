package com.rjrudin.marklogic.appdeployer.command.failover;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rjrudin.marklogic.appdeployer.command.AbstractCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.mgmt.forests.ForestManager;
import com.rjrudin.marklogic.mgmt.hosts.HostManager;

public class CreateMlAppReplicasCommand extends AbstractCommand {

    private Map<String, Integer> forestNamesAndReplicaCounts = new HashMap<>();

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

        for (String forestName : forestNamesAndReplicaCounts.keySet()) {
            int replicaCount = forestNamesAndReplicaCounts.get(forestName);
            if (replicaCount > 0) {
                doResource(forestName, replicaCount, hostIds, forestMgr);
            }
        }
    }

    protected void doResource(String primaryForestName, int replicaCount, List<String> hostIds, ForestManager forestMgr) {
        String primaryForestHostId = forestMgr.getHostId(primaryForestName);
        logger.info(primaryForestName + " host: " + primaryForestHostId);

        // Now for each other host, create N replicas on the host
        int resourceCounter = 2;
        Map<String, String> replicaNamesAndHostIds = new HashMap<>();
        for (String hostId : hostIds) {
            if (!hostId.equals(primaryForestHostId)) {
                for (int i = 0; i < replicaCount; i++) {
                    String name = primaryForestName + "-" + resourceCounter;
                    forestMgr.createForestWithName(name, hostId);
                    replicaNamesAndHostIds.put(name, hostId);
                    resourceCounter++;
                }
            }
        }

        if (!replicaNamesAndHostIds.isEmpty()) {
            forestMgr.setReplicas(primaryForestName, replicaNamesAndHostIds);
        }
    }

    public void setForestNamesAndReplicaCounts(Map<String, Integer> forestNamesAndReplicaCounts) {
        this.forestNamesAndReplicaCounts = forestNamesAndReplicaCounts;
    }

    public Map<String, Integer> getForestNamesAndReplicaCounts() {
        return forestNamesAndReplicaCounts;
    }
}
