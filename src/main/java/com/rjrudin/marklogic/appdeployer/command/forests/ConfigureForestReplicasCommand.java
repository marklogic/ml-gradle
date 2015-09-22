package com.rjrudin.marklogic.appdeployer.command.forests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rjrudin.marklogic.appdeployer.command.AbstractUndoableCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.mgmt.forests.ForestManager;
import com.rjrudin.marklogic.mgmt.hosts.HostManager;

/**
 * Command for configuring - i.e. creating and setting - replica forests for an existing primary forests. Very useful
 * for the out-of-the-box forests such as Security, Schemas, App-Services, and Meters, which normally need replicas for
 * failover in a cluster.
 */
public class ConfigureForestReplicasCommand extends AbstractUndoableCommand {

    private Map<String, Integer> forestNamesAndReplicaCounts = new HashMap<>();
    private boolean deleteReplicasOnUndo = true;

    /**
     * By default, the execute sort order is Integer.MAX_VALUE as a way of guaranteeing that the referenced primary
     * forests already exist. Feel free to customize as needed.
     */
    public ConfigureForestReplicasCommand() {
        setExecuteSortOrder(Integer.MAX_VALUE);
    }

    @Override
    public void execute(CommandContext context) {
        HostManager hostMgr = new HostManager(context.getManageClient());
        ForestManager forestMgr = new ForestManager(context.getManageClient());

        List<String> hostIds = hostMgr.getHostIds();
        if (hostIds.size() == 1) {
            if (logger.isInfoEnabled()) {
                logger.info("Only found one host ID, so not configuring any replica forests; host ID: "
                        + hostIds.get(0));
            }
            return;
        }

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
        logger.info(format("Configuring forest replicas for primary forest %s", primaryForestName));

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

        logger.info(format("Finished configuring forest replicas for primary forest %s", primaryForestName));
    }

    @Override
    public void undo(CommandContext context) {
        if (deleteReplicasOnUndo) {
            ForestManager mgr = new ForestManager(context.getManageClient());
            for (String forestName : forestNamesAndReplicaCounts.keySet()) {
                logger.info(format("Deleting forest replicas for primary forest %s", forestName));
                mgr.deleteReplicas(forestName);
                logger.info(format("Finished deleting forest replicas for primary forest %s", forestName));
            }
        } else {
            logger.info("deleteReplicasOnUndo is set to false, so not deleting any replicas");
        }
    }

    public void setForestNamesAndReplicaCounts(Map<String, Integer> forestNamesAndReplicaCounts) {
        this.forestNamesAndReplicaCounts = forestNamesAndReplicaCounts;
    }

    public Map<String, Integer> getForestNamesAndReplicaCounts() {
        return forestNamesAndReplicaCounts;
    }

    public void setDeleteReplicasOnUndo(boolean deleteReplicasOnUndo) {
        this.deleteReplicasOnUndo = deleteReplicasOnUndo;
    }
}
