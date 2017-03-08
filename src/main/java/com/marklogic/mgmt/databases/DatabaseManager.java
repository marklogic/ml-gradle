package com.marklogic.mgmt.databases;

import java.util.List;

import com.marklogic.mgmt.AbstractResourceManager;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.mgmt.forests.ForestManager;
import com.marklogic.mgmt.forests.ForestStatus;
import com.marklogic.rest.util.Fragment;

public class DatabaseManager extends AbstractResourceManager {

    private String forestDelete = "data";
    private boolean deleteReplicas = true;

    public DatabaseManager(ManageClient manageClient) {
        super(manageClient);
    }

    /**
     * This will catch and log any exception by default, as the most frequent reason why this fails is because the
     * database doesn't exist yet.
     * 
     * @param databaseIdOrName
     */
    public void clearDatabase(String databaseIdOrName) {
        clearDatabase(databaseIdOrName, true);
    }

    public void clearDatabase(String databaseIdOrName, boolean catchException) {
        try {
            invokeOperation(databaseIdOrName, "clear-database");
        } catch (Exception e) {
            if (catchException) {
                logger.error("Unable to clear database; cause: " + e.getMessage());
            } else {
                throw e;
            }
        }
    }

    public void mergeDatabase(String databaseIdOrName) {
        invokeOperation(databaseIdOrName, "merge-database");
    }

    public void reindexDatabase(String databaseIdOrName) {
        invokeOperation(databaseIdOrName, "reindex-database");
    }
    
    private void invokeOperation(String databaseIdOrName, String operation) {
        String path = format("/manage/v2/databases/%s", databaseIdOrName);
        logger.info(format("Invoking operation %s on database %s", operation, databaseIdOrName));
        getManageClient().postJson(path, format("{\"operation\":\"%s\"}", operation));
        logger.info(format("Finished invoking operation %s on database %s", operation, databaseIdOrName));
    }

    public void deleteByName(String databaseName) {
        String json = format("{\"database-name\":\"%s\"}", databaseName);
        delete(json);
    }

    @Override
    protected void beforeDelete(String resourceId, String path, String... resourceUrlParams) {
        if (deleteReplicas) {
            logger.info("Deleting any replicas that exist for database: " + resourceId);
            ForestManager forestManager = new ForestManager(getManageClient());
            for (String forestId : getPrimaryForestIds(resourceId)) {
                ForestStatus status = forestManager.getForestStatus(forestId);
                if (status.isPrimary() && status.hasReplicas()) {
                    forestManager.deleteReplicas(forestId);
                }
            }
        }
    }

    /**
     * @param databaseNameOrId
     * @return the IDs of all forests - primary and replica - related to the database
     */
    public List<String> getForestIds(String databaseNameOrId) {
        Fragment f = getAsXml(databaseNameOrId);
        return f.getElementValues("/node()/db:relations/db:relation-group[db:typeref='forests']/db:relation/db:idref");
    }

    /**
     * @param databaseNameOrId
     * @return the names of all forests - primary and replica - related to the database
     */
    public List<String> getForestNames(String databaseNameOrId) {
        Fragment f = getAsXml(databaseNameOrId);
        return f.getElementValues(
                "/node()/db:relations/db:relation-group[db:typeref='forests']/db:relation/db:nameref");
    }

    /**
     * @param databaseNameOrId
     * @return the IDs of all primary forests related to the database. The properties endpoint for a database lists
     *         primary forest IDs, but not replica forest IDs.
     */
    public List<String> getPrimaryForestIds(String databaseNameOrId) {
        return getPropertiesAsXml(databaseNameOrId).getElementValues("/node()/m:forests/m:forest");
    }

    public void deleteReplicaForests(String databaseNameOrId) {
        logger.info(format("Deleting replica forests (if any exist) for database %s", databaseNameOrId));
        ForestManager mgr = new ForestManager(getManageClient());
        for (String forestId : getPrimaryForestIds(databaseNameOrId)) {
            mgr.deleteReplicas(forestId);
        }
        logger.info(format("Finished deleting replica forests for database %s", databaseNameOrId));
    }

    /**
     * TODO Not sure, when setting updates-allowed on primary forests, if replica forests need to have their
     * updates-allowed set as well.
     *
     * @param databaseNameOrId
     */
    public void setUpdatesAllowedOnPrimaryForests(String databaseNameOrId, String mode) {
        ForestManager mgr = new ForestManager(getManageClient());
        for (String forestId : getPrimaryForestIds(databaseNameOrId)) {
            mgr.setUpdatesAllowed(forestId, mode);
        }
    }

    @Override
    protected String[] getDeleteResourceParams(String payload) {
        return forestDelete != null ? new String[] { "forest-delete", forestDelete } : new String[] {};
    }

    public void setForestDelete(String forestDelete) {
        this.forestDelete = forestDelete;
    }

    public boolean isDeleteReplicas() {
        return deleteReplicas;
    }

    public void setDeleteReplicas(boolean deleteReplicas) {
        this.deleteReplicas = deleteReplicas;
    }
}
