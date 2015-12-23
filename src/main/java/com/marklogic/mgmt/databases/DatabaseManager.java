package com.marklogic.mgmt.databases;

import java.util.ArrayList;
import java.util.List;

import com.marklogic.mgmt.AbstractResourceManager;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.forests.ForestManager;
import com.marklogic.rest.util.Fragment;

public class DatabaseManager extends AbstractResourceManager {

    private String forestDelete = "data";

    public DatabaseManager(ManageClient manageClient) {
        super(manageClient);
    }

    public void clearDatabase(String databaseIdOrName) {
        String path = format("/manage/v2/databases/%s", databaseIdOrName);
        logger.info(format("Clearing database %s", databaseIdOrName));
        try {
            getManageClient().postJson(path, "{\"operation\":\"clear-database\"}");
            logger.info(format("Cleared database %s", databaseIdOrName));
        } catch (Exception e) {
            logger.error("Unable to clear database; cause: " + e.getMessage());
        }
    }

    public void deleteByName(String databaseName) {
        String json = format("{\"database-name\":\"%s\"}", databaseName);
        delete(json);
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
        return f.getElementValues("/node()/db:relations/db:relation-group[db:typeref='forests']/db:relation/db:nameref");
    }

    /**
     * @param databaseNameOrId
     * @return the IDs of all primary forests related to the database. A primary forest is any forest that has replica
     *         forests configured for it.
     */
    public List<String> getPrimaryForestIds(String databaseNameOrId) {
        List<String> primaryForestIds = new ArrayList<String>();
        ForestManager mgr = new ForestManager(getManageClient());
        for (String forestId : getForestIds(databaseNameOrId)) {
            if (!mgr.getReplicaIds(forestId).isEmpty()) {
                primaryForestIds.add(forestId);
            }
        }
        return primaryForestIds;
    }

    public void deleteReplicaForests(String databaseNameOrId) {
        logger.info(format("Deleting replica forests (if any exist) for database %s", databaseNameOrId));
        ForestManager mgr = new ForestManager(getManageClient());
        for (String forestId : getPrimaryForestIds(databaseNameOrId)) {
            mgr.deleteReplicas(forestId);
        }
        logger.info(format("Finished deleting replica forests for database %s", databaseNameOrId));
    }

    @Override
    protected String[] getDeleteResourceParams(String payload) {
        return forestDelete != null ? new String[] { "forest-delete", forestDelete } : new String[] {};
    }

    public void setForestDelete(String forestDelete) {
        this.forestDelete = forestDelete;
    }
}
