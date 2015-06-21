package com.marklogic.rest.mgmt.databases;

import java.util.List;

import com.marklogic.rest.mgmt.AbstractResourceManager;
import com.marklogic.rest.mgmt.ManageClient;
import com.marklogic.rest.util.Fragment;

public class DatabaseManager extends AbstractResourceManager {

    private String forestDelete = "data";

    public DatabaseManager(ManageClient manageClient) {
        super(manageClient);
    }

    public void clearDatabase(String databaseIdOrName) {
        String path = format("/manage/v2/databases/%s", databaseIdOrName);
        logger.info(format("Clearing database %s", databaseIdOrName));
        getManageClient().postJson(path, "{\"operation\":\"clear-database\"}");
        logger.info(format("Cleared database %s", databaseIdOrName));
    }

    public List<String> getForestIds(String databaseNameOrId) {
        Fragment f = getAsXml(databaseNameOrId);
        return f.getElementValues("/node()/db:relations/db:relation-group[db:typeref='forests']/db:relation/db:idref");
    }

    @Override
    protected String[] getDeleteResourceParams(String payload) {
        return forestDelete != null ? new String[] { "forest-delete", forestDelete } : new String[] {};
    }

    public void setForestDelete(String forestDelete) {
        this.forestDelete = forestDelete;
    }
}
