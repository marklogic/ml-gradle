/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.mgmt.resource.databases;

import com.marklogic.mgmt.resource.AbstractResourceManager;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.forests.ForestManager;
import com.marklogic.rest.util.Fragment;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager extends AbstractResourceManager {

	public final static String DELETE_FOREST_DATA = "data";
	public final static String DELETE_FOREST_CONFIGURATION = "configuration";

    private String forestDelete = DELETE_FOREST_DATA;
    private boolean deleteReplicas = true;

    public DatabaseManager(ManageClient manageClient) {
        super(manageClient);
    }

	/**
	 * This is being added in 3.9.1 to add support for when a database is updated by a user that only has privileges
	 * to update indexes. In such a scenario, the database-name property cannot exist, even if it's not changing. It's
	 * also not needed, because the URL for the request specifies the database to update.
	 *
	 * It may be safe to make this change for all resource property updates, but it's only being applied for databases
	 * since that's the immediate need for the Data Hub Framework.
	 *
	 * @param client
	 * @param path
	 * @param payload
	 * @return
	 */
	@Override
	protected ResponseEntity<String> putPayload(ManageClient client, String path, String payload) {
		if (payloadParser.isJsonPayload(payload)) {
			payload = payloadParser.excludeProperties(payload, getIdFieldName());
		}
		return super.putPayload(client, path, payload);
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

    public String getResourceId(String payload){
    	return super.getResourceId(payload);
    }

    /**
     * Detaches or disassociates any sub-databases from this database
     * @param databaseIdOrName
     */
    public void detachSubDatabases(String databaseIdOrName){
    	logger.info("Detaching sub-databases from database: " + databaseIdOrName);
    	save(format("{\"database-name\":\"%s\", \"subdatabase\": []}", databaseIdOrName));
    	logger.info("Finished detaching sub-databases from database: " + databaseIdOrName);
    }

    /**
     * Attaches/associates the specified databases with this database, making it a super-database.
     * Note: that the databases listed in subDbNames must have already been created.
     *
     * @param databaseIdOrName
     * @param subDbNames
     */
    public void attachSubDatabases(String databaseIdOrName, List<String> subDbNames){
    	StringBuilder payload = new StringBuilder(format("{\"database-name\":\"%s\", \"subdatabase\": [", databaseIdOrName));
    	for(int index = 0; index < subDbNames.size(); index++){
    		if(index > 0){ payload.append(","); }
    		payload.append(format("{\"database-name\":\"%s\"}", subDbNames.get(index)));
    	}
    	payload.append("]}");
    	logger.info("Attaching sub-databases to database: " + databaseIdOrName + ", using configured payload: " + payload);
    	save(payload.toString());
    	logger.info("Finished attaching sub-databases to database: " + databaseIdOrName);

    }

    public List<String> getSubDatabases(String databaseNameOrId) {
    	return getPropertiesAsXml(databaseNameOrId).getElementValues("/node()/m:subdatabases/m:subdatabase/m:database-name");
    }

    public void deleteByName(String databaseName) {
        String json = format("{\"database-name\":\"%s\"}", databaseName);
        delete(json);
    }

    /**
     * Use this to delete all of a database's forests and their replicas, but leave the database in place. This seems
     * to be the safest way to ensure a database can be deleted.
     *
     * @param databaseIdOrName
     */
    public void deleteForestsAndReplicas(String databaseIdOrName) {
        List<String> primaryForestIds = getPrimaryForestIds(databaseIdOrName);
        detachForests(databaseIdOrName);
        ForestManager forestManager = new ForestManager(getManageClient());
        for (String forestId : primaryForestIds) {
            forestManager.delete(forestId, ForestManager.DELETE_LEVEL_FULL, ForestManager.REPLICAS_DELETE);
        }
    }

    public void detachForests(String databaseIdOrName) {
    	logger.info("Detaching forests from database: " + databaseIdOrName);
        save(format("{\"database-name\":\"%s\", \"forest\":[]}", databaseIdOrName));
        logger.info("Finished detaching forests from database: " + databaseIdOrName);
    }

    @Override
    protected void beforeDelete(String resourceId, String path, String... resourceUrlParams) {
        if (deleteReplicas) {
        	logger.info("Deleting forests and replicas for database: " + resourceId);
        	deleteForestsAndReplicas(resourceId);
        	logger.info("Finished deleting forests and replicas for database: " + resourceId);
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
    	if (exists(databaseNameOrId)) {
		    return getPropertiesAsXml(databaseNameOrId).getElementValues("/node()/m:forests/m:forest");
	    } else {
    		return new ArrayList<>();
	    }
    }

	/**
	 * Delete all replicas for the primary forests for the given database, but don't delete the database or the
	 * primary forests.
	 *
	 * @param databaseNameOrId
	 */
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

	public String getForestDelete() {
		return forestDelete;
	}

	public boolean isDeleteReplicas() {
        return deleteReplicas;
    }

    public void setDeleteReplicas(boolean deleteReplicas) {
        this.deleteReplicas = deleteReplicas;
    }
}
