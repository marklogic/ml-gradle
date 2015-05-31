package com.marklogic.appdeployer.mgmt.databases;

import com.marklogic.appdeployer.mgmt.ManageClient;
import com.marklogic.appdeployer.util.Fragment;
import com.marklogic.clientutil.LoggingObject;

public class DatabaseManager extends LoggingObject {

    private ManageClient client;

    public DatabaseManager(ManageClient client) {
        this.client = client;
    }

    public void createDatabase(String name, String payload) {
        if (dbExists(name)) {
            logger.warn("Database already exists: " + name);
        } else {
            logger.info("Creating database: " + name);
            client.postJson("/manage/v2/databases", payload);
            logger.info("Created database: " + name);
        }
    }

    public boolean dbExists(String name) {
        Fragment f = client.getXml("/manage/v2/databases", "db", "http://marklogic.com/manage/databases");
        return f.elementExists(String.format("/db:database-default-list/db:list-items/db:list-item[db:nameref = '%s']",
                name));
    }
}
