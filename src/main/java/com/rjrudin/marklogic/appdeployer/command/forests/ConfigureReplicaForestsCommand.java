package com.rjrudin.marklogic.appdeployer.command.forests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rjrudin.marklogic.appdeployer.command.AbstractCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.mgmt.forests.ForestManager;
import com.rjrudin.marklogic.mgmt.hosts.HostManager;

/**
 * Command for configuring - i.e. creating and setting - replica forests for an existing primary forests. Very useful
 * for the out-of-the-box forests such as Security, Schemas, App-Services, and Meters, which normally need replicas for
 * failover in a cluster.
 */
public class ConfigureReplicaForestsCommand extends AbstractCommand {

    private Map<String, Integer> forestNamesAndReplicaCounts = new HashMap<>();

    /**
     * By default, the execute sort order is Integer.MAX_VALUE as a way of guaranteeing that the referenced primary
     * forests already exist. Feel free to customize as needed.
     */
    public ConfigureReplicaForestsCommand() {
        setExecuteSortOrder(Integer.MAX_VALUE);
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
                configureReplicaForests(forestName, replicaCount, hostIds, forestMgr);
            }
        }
    }

    /**
     * Creates forests as needed (they may already exists) and then sets those forests as the replicas for the given
     * primaryForestName.
     * 
     * @param primaryForestName
     * @param replicaCount
     * @param hostIds
     * @param forestMgr
     */
    protected void configureReplicaForests(String primaryForestName, int replicaCount, List<String> hostIds,
            ForestManager forestMgr) {
        String primaryForestHostId = forestMgr.getHostId(primaryForestName);

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
