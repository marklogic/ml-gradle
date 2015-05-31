package com.marklogic.appdeployer.mgmt.databases;

import com.marklogic.appdeployer.mgmt.AbstractManager;
import com.marklogic.appdeployer.mgmt.ManageClient;
import com.marklogic.appdeployer.util.Fragment;

public class DatabaseManager extends AbstractManager {

    private ManageClient client;

    public DatabaseManager(ManageClient client) {
        this.client = client;
    }

    public void createDatabase(String name, String payload) {
        if (dbExists(name)) {
            logger.info(format("Database %s already exists", name));
        } else {
            logger.info(format("Creating database: %s", name));
            client.postJson("/manage/v2/databases", payload);
            logger.info(format("Created database: %s", name));
        }
    }

    public boolean dbExists(String name) {
        Fragment f = client.getXml("/manage/v2/databases", "db", "http://marklogic.com/manage/databases");
        return f.elementExists(String.format("/db:database-default-list/db:list-items/db:list-item[db:nameref = '%s']",
                name));
    }

    public void deleteDatabase(String name) {
        if (!dbExists(name)) {
            logger.info(format("Database %s name does not exist, not deleting", name));
        } else {
            logger.info(format("Deleting database %s", name));
            // This is deleting the forests too, though the docs -
            // http://docs.marklogic.com/REST/DELETE/manage/v2/databases/[id-or-name] - suggest otherwise
            client.delete(format("/manage/v2/databases/%s", name));
            logger.info(format("Deleted database %s", name));
        }
    }
}
