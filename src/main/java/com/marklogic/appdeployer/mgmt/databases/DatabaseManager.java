package com.marklogic.appdeployer.mgmt.databases;

import com.marklogic.appdeployer.AbstractManager;
import com.marklogic.appdeployer.mgmt.ManageClient;
import com.marklogic.appdeployer.util.Fragment;

public class DatabaseManager extends AbstractManager {

    private ManageClient manageClient;

    public DatabaseManager(ManageClient manageClient) {
        this.manageClient = manageClient;
    }

    public void createDatabase(String name, String payload) {
        if (dbExists(name)) {
            logger.info(format("Database %s already exists", name));
        } else {
            logger.info(format("Creating database: %s", name));
            manageClient.postJson("/manage/v2/databases", payload);
            logger.info(format("Created database: %s", name));
        }
    }

    public boolean dbExists(String name) {
        Fragment f = manageClient.getXml("/manage/v2/databases", "db", "http://marklogic.com/manage/databases");
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
            manageClient.delete(format("/manage/v2/databases/%s", name));
            logger.info(format("Deleted database %s", name));
        }
    }

    /**
     * This might be too fine-grained, we may want to just do this as part of updating the content database.
     */
    public void assignTriggersDatabase(String databaseIdOrName, String triggersDatabaseName) {
        String json = format("{\"triggers-database\":\"%s\"}", triggersDatabaseName);
        logger.info(format("Assigning triggers database %s to database %s", triggersDatabaseName, databaseIdOrName));
        updateDatabase(databaseIdOrName, json);
        logger.info(format("Assigned triggers database %s to database %s", triggersDatabaseName, databaseIdOrName));
    }

    public void updateDatabase(String databaseIdOrName, String json) {
        String path = format("/manage/v2/databases/%s/properties", databaseIdOrName);
        // TODO Log the JSON at debug level?
        logger.info(format("Updating database %s", databaseIdOrName));
        manageClient.putJson(path, json);
        logger.info(format("Updated database %s", databaseIdOrName));
    }
}
