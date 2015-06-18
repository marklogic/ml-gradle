package com.marklogic.rest.mgmt.databases;

import com.marklogic.rest.mgmt.AbstractResourceManager;
import com.marklogic.rest.mgmt.ManageClient;

public class DatabaseManager extends AbstractResourceManager {

    public DatabaseManager(ManageClient manageClient) {
        super(manageClient);
    }

    public void clearDatabase(String databaseIdOrName) {
        String path = format("/manage/v2/databases/%s", databaseIdOrName);
        logger.info(format("Clearing database %s", databaseIdOrName));
        getManageClient().postJson(path, "{\"operation\":\"clear-database\"}");
        logger.info(format("Cleared database %s", databaseIdOrName));
    }
}
