package com.rjrudin.marklogic.appdeployer.command.forests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rjrudin.marklogic.appdeployer.command.AbstractUndoableCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.mgmt.databases.DatabaseManager;
import com.rjrudin.marklogic.mgmt.forests.ForestManager;
import com.rjrudin.marklogic.mgmt.forests.ForestStatus;
import com.rjrudin.marklogic.mgmt.hosts.HostManager;

/**
 * Command for configuring - i.e. creating and setting - replica forests for existing databases and/or primary forests.
 * It's normally easier to just specify the databases that you want to configure forest replicas for, but this command
 * does provide the ability to configure replicas for specific forests.
 * 
 * Very useful for the out-of-the-box forests such as Security, Schemas, App-Services, and Meters, which normally need
 * replicas for failover in a cluster.
 */
public class ConfigureForestReplicasCommand extends AbstractUndoableCommand {

    private Map<String, Integer> databaseNamesAndReplicaCounts = new HashMap<>();
    private Map<String, Integer> forestNamesAndReplicaCounts = new HashMap<>();
    private boolean deleteReplicasOnUndo = true;

    /**
     * By default, the execute sort order is Integer.MAX_VALUE as a way of guaranteeing that the referenced primary
     * forests already exist. Feel free to customize as needed.
     */
    public ConfigureForestReplicasCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_FOREST_REPLICAS);
        setUndoSortOrder(SortOrderConstants.DELETE_FOREST_REPLICAS);
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

        for (String databaseName : databaseNamesAndReplicaCounts.keySet()) {
            int replicaCount = databaseNamesAndReplicaCounts.get(databaseName);
            if (replicaCount > 0) {
                configureDatabaseReplicaForests(databaseName, replicaCount, hostIds, context);
            }
        }

        for (String forestName : forestNamesAndReplicaCounts.keySet()) {
            int replicaCount = forestNamesAndReplicaCounts.get(forestName);
            if (replicaCount > 0) {
                configureReplicaForests(forestName, replicaCount, hostIds, forestMgr);
            }
        }
    }

    @Override
    public void undo(CommandContext context) {
        if (deleteReplicasOnUndo) {
            DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());
            ForestManager forestMgr = new ForestManager(context.getManageClient());

            for (String databaseName : databaseNamesAndReplicaCounts.keySet()) {
                logger.info(format("Deleting forest replicas for database %s", databaseName));
                for (String forestName : dbMgr.getForestNames(databaseName)) {
                    deleteReplicas(forestName, forestMgr);
                }
                logger.info(format("Finished deleting forest replicas for database %s", databaseName));
            }

            for (String forestName : forestNamesAndReplicaCounts.keySet()) {
                deleteReplicas(forestName, forestMgr);
            }
        } else {
            logger.info("deleteReplicasOnUndo is set to false, so not deleting any replicas");
        }
    }

    protected void deleteReplicas(String forestName, ForestManager forestMgr) {
        if (forestMgr.exists(forestName)) {
            ForestStatus status = forestMgr.getForestStatus(forestName);
            if (status.isPrimary() && status.hasReplicas()) {
                logger.info(format("Deleting forest replicas for primary forest %s", forestName));
                forestMgr.deleteReplicas(forestName);
                logger.info(format("Finished deleting forest replicas for primary forest %s", forestName));
            }
        }
    }

    /**
     * For the given database, find all of its primary forests. Then for each primary forest, just call
     * configureReplicaForests? And that should be smart enough to say - if the primary forest already has replicas,
     * then don't do anything.
     * 
     * @param databaseName
     * @param replicaCount
     * @param hostIds
     */
    protected void configureDatabaseReplicaForests(String databaseName, int replicaCount, List<String> hostIds,
            CommandContext context) {
        ForestManager forestMgr = new ForestManager(context.getManageClient());
        DatabaseManager dbMgr = new DatabaseManager(context.getManageClient());
        List<String> forestNames = dbMgr.getForestNames(databaseName);
        logger.info("Forests: " + forestNames);
        for (String name : forestNames) {
            configureReplicaForests(name, replicaCount, hostIds, forestMgr);
        }
    }

    /**
     * Creates forests as needed (they may already exists) and then sets those forests as the replicas for the given
     * primaryForestName.
     * 
     * @param forestIdOrName
     * @param replicaCount
     * @param hostIds
     * @param forestMgr
     */
    protected void configureReplicaForests(String forestIdOrName, int replicaCount, List<String> hostIds,
            ForestManager forestMgr) {
        ForestStatus status = forestMgr.getForestStatus(forestIdOrName);
        if (!status.isPrimary()) {
            logger.info(format("Forest %s is not a primary forest, so not configuring replica forests", forestIdOrName));
            return;
        }
        if (status.hasReplicas()) {
            logger.info(format("Forest %s already has replicas, so not configuring replica forests", forestIdOrName));
            return;
        }

        logger.info(format("Configuring forest replicas for primary forest %s", forestIdOrName));
        String primaryForestHostId = forestMgr.getHostId(forestIdOrName);

        int resourceCounter = 2;
        Map<String, String> replicaNamesAndHostIds = new HashMap<>();
        for (String hostId : hostIds) {
            if (!hostId.equals(primaryForestHostId)) {
                for (int i = 0; i < replicaCount; i++) {
                    String name = forestIdOrName + "-" + resourceCounter;
                    forestMgr.createForestWithName(name, hostId);
                    replicaNamesAndHostIds.put(name, hostId);
                    resourceCounter++;
                }
            }
        }

        if (!replicaNamesAndHostIds.isEmpty()) {
            forestMgr.setReplicas(forestIdOrName, replicaNamesAndHostIds);
        }
        logger.info(format("Finished configuring forest replicas for primary forest %s", forestIdOrName));
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

    public Map<String, Integer> getDatabaseNamesAndReplicaCounts() {
        return databaseNamesAndReplicaCounts;
    }

    public void setDatabaseNamesAndReplicaCounts(Map<String, Integer> databaseNamesAndReplicaCounts) {
        this.databaseNamesAndReplicaCounts = databaseNamesAndReplicaCounts;
    }
}
