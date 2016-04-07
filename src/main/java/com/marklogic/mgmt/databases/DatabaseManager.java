package com.marklogic.mgmt.databases;

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
     * @return the IDs of all primary forests related to the database. The properties endpoint for a database lists
     * primary forest IDs, but not replica forest IDs.
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
}
